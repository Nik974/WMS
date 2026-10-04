package com.backend.wms.service;

import com.backend.wms.dto.StockDto;
import com.backend.wms.entity.Batch;
import com.backend.wms.entity.Location;
import com.backend.wms.entity.Product;
import com.backend.wms.entity.Stock;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.StockMapper;
import com.backend.wms.repository.BatchRepository;
import com.backend.wms.repository.LocationRepository;
import com.backend.wms.repository.ProductRepository;
import com.backend.wms.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public StockDto createStock(StockDto dto) {
        log.info("Creating new stock record for Product ID: {}", dto.productId());

        Location location = findLocationById(dto.locationId());
        Product product = findProductById(dto.productId());
        Batch batch = findBatchById(dto.batchId());

        Stock stock = stockMapper.toEntityWithDependencies(dto, location, product, batch);
        Stock savedStock = stockRepository.save(stock);
        return stockMapper.toDto(savedStock);
    }

    @Transactional
    public StockDto updateStock(Long id, StockDto dto) {
        log.info("Updating stock record with ID: {}", id);

        Stock existingStock = findStockById(id);
        Location location = findLocationById(dto.locationId());
        Product product = findProductById(dto.productId());
        Batch batch = findBatchById(dto.batchId());

        stockMapper.updateEntity(dto, location, product, batch, existingStock);
        Stock updatedStock = stockRepository.save(existingStock);
        return stockMapper.toDto(updatedStock);
    }

    public StockDto getStockById(Long id) {
        return stockMapper.toDto(findStockById(id));
    }

    public List<StockDto> getStocksByProductId(Long productId) {
        List<Stock> stocks = stockRepository.findAllByProductId(productId);
        return stockMapper.toDtoList(stocks);
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
}