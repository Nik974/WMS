package com.backend.wms.service;

import com.backend.wms.dto.CreateInboundOrderDto;
import com.backend.wms.dto.InboundItemDto;
import com.backend.wms.dto.InboundOrderDto;
import com.backend.wms.dto.ReceiveInboundRequest;
import com.backend.wms.entity.*;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.InboundOrderMapper;
import com.backend.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboundOrderService {

    private final InboundOrderRepository inboundOrderRepository;
    private final InboundOrderMapper inboundOrderMapper;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final BatchRepository batchRepository;
    private final StockService stockService;

    private static final Map<InboundStatus, Set<InboundStatus>> ALLOWED = Map.of(
            InboundStatus.NEW, Set.of(InboundStatus.RECEIVED, InboundStatus.CANCELLED),
            InboundStatus.RECEIVED, Set.of(),
            InboundStatus.CANCELLED, Set.of()
    );

    public InboundOrderDto getInboundOrderById(Long id) {
        return inboundOrderRepository.findById(id)
                .map(inboundOrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));
    }

    public List<InboundOrderDto> getAllInboundOrders() {
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAll());
    }

    public List<InboundOrderDto> getInboundOrdersBySupplierId(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllBySupplierId(supplierId));
    }

    public List<InboundOrderDto> getInboundOrdersByWarehouseId(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + warehouseId);
        }
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllByWarehouseId(warehouseId));
    }

    public List<InboundOrderDto> getInboundOrdersByStatus(InboundStatus status) {
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllByStatus(status));
    }

    public List<InboundOrderDto> getInboundOrdersByCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllByCreatedAtBetween(start, end));
    }

    @Transactional
    public InboundOrderDto createInboundOrder(CreateInboundOrderDto dto) {
        Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + dto.warehouseId()));
        Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + dto.supplierId()));

        Set<Long> productIds = dto.items().stream()
                .map(InboundItemDto::productId)
                .collect(Collectors.toSet());
        Map<Long, Product> products = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        if (products.size() != productIds.size()) {
            throw new ResourceNotFoundException("Some products not found");
        }

        InboundOrder order = new InboundOrder();
        order.setWarehouse(warehouse);
        order.setSupplier(supplier);
        order.setStatus(InboundStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        for (InboundItemDto i : dto.items()) {
            InboundItem item = new InboundItem();
            item.setOrder(order);
            item.setProduct(products.get(i.productId()));
            item.setQuantity(i.quantity());
            order.getItems().add(item);
        }

        return inboundOrderMapper.toDto(inboundOrderRepository.save(order));
    }

    @Transactional
    public InboundOrderDto editInboundOrder(Long id, InboundOrderDto dto) {
        InboundOrder order = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));

        if (order.getStatus() != InboundStatus.NEW) {
            throw new IllegalStateException("Only orders in NEW status can be edited");
        }

        order.setWarehouse(warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + dto.warehouseId())));
        order.setSupplier(supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + dto.supplierId())));

        return inboundOrderMapper.toDto(order);
    }

    @Transactional
    public InboundOrderDto changeStatus(Long id, InboundStatus newStatus) {
        if (newStatus == InboundStatus.RECEIVED) {
            throw new IllegalArgumentException("Use POST /{id}/receive to receive an order");
        }
        InboundOrder order = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));

        if (!ALLOWED.get(order.getStatus()).contains(newStatus)) {
            throw new IllegalStateException("Cannot change status from " + order.getStatus() + " to " + newStatus);
        }
        order.setStatus(newStatus);
        return inboundOrderMapper.toDto(order);
    }

    @Transactional
    public InboundOrderDto receive(Long id, ReceiveInboundRequest request, Long userId) {
        InboundOrder order = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));

        if (order.getStatus() != InboundStatus.NEW) {
            throw new IllegalStateException("Only orders in NEW status can be received");
        }

        Map<Long, ReceiveInboundRequest.Line> lines = new HashMap<>();
        for (ReceiveInboundRequest.Line l : request.lines()) {
            if (lines.put(l.itemId(), l) != null) {
                throw new IllegalArgumentException("Duplicate line for item " + l.itemId());
            }
        }
        Set<Long> itemIds = order.getItems().stream().map(InboundItem::getId).collect(Collectors.toSet());
        if (!lines.keySet().equals(itemIds)) {
            throw new IllegalArgumentException("Every item of the order needs exactly one line");
        }

        Map<Long, Location> locations = locationRepository.findAllById(
                        lines.values().stream().map(ReceiveInboundRequest.Line::locationId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Location::getId, l -> l));
        Map<Long, Batch> batches = batchRepository.findAllById(
                        lines.values().stream().map(ReceiveInboundRequest.Line::batchId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Batch::getId, b -> b));

        for (InboundItem item : order.getItems()) {
            ReceiveInboundRequest.Line line = lines.get(item.getId());

            Location location = locations.get(line.locationId());
            if (location == null)
                throw new ResourceNotFoundException("Location not found with id: " + line.locationId());
            Batch batch = batches.get(line.batchId());
            if (batch == null) throw new ResourceNotFoundException("Batch not found with id: " + line.batchId());

            if (!location.getWarehouse().getId().equals(order.getWarehouse().getId())) {
                throw new IllegalArgumentException("Location " + location.getCode() + " is not in the order's warehouse");
            }
            if (!batch.getProduct().getId().equals(item.getProduct().getId())) {
                throw new IllegalArgumentException("Batch " + batch.getLotNumber() + " does not belong to the product of the item");
            }

            stockService.receive(location, item.getProduct(), batch, item.getQuantity(), order.getId(), userId);
        }

        order.setStatus(InboundStatus.RECEIVED);
        return inboundOrderMapper.toDto(order);
    }
}
