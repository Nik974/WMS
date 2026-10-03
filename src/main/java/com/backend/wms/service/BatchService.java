package com.backend.wms.service;

import com.backend.wms.dto.BatchDto;
import com.backend.wms.entity.Batch;
import com.backend.wms.entity.Product;
import com.backend.wms.entity.Supplier;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.BatchMapper;
import com.backend.wms.repository.BatchRepository;
import com.backend.wms.repository.ProductRepository;
import com.backend.wms.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BatchService {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @Transactional
    public BatchDto createBatch(BatchDto dto) {
        Product product = getProduct(dto.productId());
        Supplier supplier = getSupplierOrNull(dto.supplierId());

        Batch batch = batchMapper.toEntity(dto, product, supplier);
        return batchMapper.toDto(batchRepository.save(batch));
    }

    @Transactional
    public BatchDto updateBatch(Long id, BatchDto dto) {

        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));

        Supplier supplier = getSupplierOrNull(dto.supplierId());
        batchMapper.updateEntity(dto, supplier, batch);

        return batchMapper.toDto(batch);
    }

    public List<BatchDto> getAllBatches() {
        return batchMapper.toDtoList(batchRepository.findAll());
    }

    public List<BatchDto> getBatchesByProductId(Long productId) {
        return batchMapper.toDtoList(batchRepository.findAllByProductId(productId));
    }

    public List<BatchDto> getBatchesByExpiryDateBefore(java.time.LocalDate date) {
        return batchMapper.toDtoList(batchRepository.findAllByExpiryDateBefore(date));
    }

    public BatchDto getBatchByLotNumber(String lotNumber) {
        Batch batch = batchRepository.findByLotNumber(lotNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with lot number: " + lotNumber));
        return batchMapper.toDto(batch);
    }

    public BatchDto getBatchById(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));
        return batchMapper.toDto(batch);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private Supplier getSupplierOrNull(Long supplierId) {
        if (supplierId == null) {
            return null;
        }
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));
    }
}
