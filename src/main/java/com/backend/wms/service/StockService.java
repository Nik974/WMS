package com.backend.wms.service;

import com.backend.wms.dto.AdjustStockRequest;
import com.backend.wms.dto.StockDto;
import com.backend.wms.entity.*;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.StockMapper;
import com.backend.wms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final StockMovementRepository movementRepository;

    public StockDto getStockById(Long id) {
        return stockRepository.findById(id)
                .map(stockMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id));
    }

    public List<StockDto> getStocks(Long productId, Long warehouseId) {
        return stockMapper.toDtoList(stockRepository.search(productId, warehouseId));
    }

    private Stock findStockById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id));
    }

    private Location findLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Batch findBatchById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));
    }

    private void record(Stock stock, String type, int quantity, boolean incoming,
                        String refType, Long refId, String comment, Long userId) {
        StockMovement m = new StockMovement();
        m.setProduct(stock.getProduct());
        m.setBatch(stock.getBatch());
        m.setFromLocation(incoming ? null : stock.getLocation());
        m.setToLocation(incoming ? stock.getLocation() : null);
        m.setMovementType(type);
        m.setQuantity(quantity);
        m.setReferenceType(refType);
        m.setReferenceId(refId);
        m.setComment(comment);
        m.setUser(userRepository.getReferenceById(userId));
        movementRepository.save(m);
        log.info("Stock movement {} {} for product {} at location {}",
                type, quantity, stock.getProduct().getId(), stock.getLocation().getId());
    }

    @Transactional
    public void receive(Location location, Product product, Batch batch, int quantity, Long inboundOrderId, Long userId) {
        Stock stock = lockOrCreate(location, product, batch);
        stock.setQuantity(stock.getQuantity() + quantity);
        record(stock, StockMovement.RECEIPT, quantity, true, "INBOUND", inboundOrderId, null, userId);
    }

    @Transactional
    public void ship(OutboundOrder order, Long userId) {
        LocalDate today = LocalDate.now();
        for (OutboundItem item : order.getItems()) {
            int remaining = item.getQuantity();
            List<Stock> candidates = stockRepository.findAvailableForShipment(
                    item.getProduct().getId(), order.getWarehouse().getId(), today);

            for (Stock stock : candidates) {
                if (remaining == 0) break;
                int take = Math.min(remaining, stock.getQuantity() - stock.getReservedQuantity());
                stock.setQuantity(stock.getQuantity() - take);
                record(stock, StockMovement.SHIPMENT, take, false, "OUTBOUND", order.getId(), null, userId);
                remaining -= take;
            }

            if (remaining > 0) {
                throw new IllegalStateException("Not enough stock for product '"
                        + item.getProduct().getProductName() + "': missing " + remaining);
            }
        }
    }

    @Transactional
    public StockDto adjust(AdjustStockRequest req, Long userId) {
        Location location = locationRepository.findById(req.locationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + req.locationId()));
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + req.productId()));
        Batch batch = batchRepository.findById(req.batchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + req.batchId()));

        if (!batch.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("Batch does not belong to this product");
        }

        Stock stock = lockOrCreate(location, product, batch);
        if (req.newQuantity() < stock.getReservedQuantity()) {
            throw new IllegalStateException("Quantity cannot be lower than reserved (" + stock.getReservedQuantity() + ")");
        }
        int delta = req.newQuantity() - stock.getQuantity();
        if (delta == 0) {
            throw new IllegalArgumentException("Quantity is unchanged");
        }

        stock.setQuantity(req.newQuantity());
        boolean incoming = delta > 0;
        record(stock, StockMovement.ADJUSTMENT, Math.abs(delta), incoming, "MANUAL", null, req.reason().trim(), userId);
        return stockMapper.toDto(stock);
    }

    private Stock lockOrCreate(Location location, Product product, Batch batch) {
        return stockRepository.findForUpdate(location.getId(), product.getId(), batch.getId())
                .orElseGet(() -> {
                    Stock s = new Stock();
                    s.setLocation(location);
                    s.setProduct(product);
                    s.setBatch(batch);
                    s.setQuantity(0);
                    s.setReservedQuantity(0);
                    return stockRepository.saveAndFlush(s);
                });
    }
}