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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @InjectMocks
    private BatchService batchService;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private BatchMapper batchMapper;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void createBatch_ShouldSaveAndReturnDto_WhenProductExistsAndSupplierIsNull() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setProductName("Test Product");

        BatchDto inputDto = new BatchDto(
                null, productId,"BATCH123", LocalDate.now().plusDays(30),null);

        Batch mappedEntity = new Batch();
        mappedEntity.setLotNumber("BATCH123");
        mappedEntity.setProduct(product);

        Batch savedEntity = new Batch();
        savedEntity.setId(100L);
        savedEntity.setLotNumber("BATCH123");
        savedEntity.setProduct(product);

        BatchDto expectedDto = new BatchDto(
                100L,productId,"BATCH123",LocalDate.now().plusDays(30),null);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        when(batchMapper.toEntity(inputDto, product, null)).thenReturn(mappedEntity);
        when(batchRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(batchMapper.toDto(savedEntity)).thenReturn(expectedDto);

        BatchDto result = batchService.createBatch(inputDto);

        assertEquals(expectedDto, result);

        verify(productRepository).findById(productId);
        verifyNoInteractions(supplierRepository);
    }

    @Test
    void createBatch_ShouldSaveAndReturnDto_WhenProductAndSupplierExist() {
        Long productId = 1L;
        Long supplierId = 5L;

        Product product = new Product();
        product.setId(productId);

        Supplier supplier = new Supplier();
        supplier.setId(supplierId);

        BatchDto inputDto = new BatchDto(
                null, productId, "BATCH123", LocalDate.now().plusDays(30), supplierId);

        Batch mappedEntity = new Batch();
        Batch savedEntity = new Batch();
        BatchDto expectedDto = new BatchDto(
                100L, productId, "BATCH123", LocalDate.now().plusDays(30), supplierId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));

        when(batchMapper.toEntity(inputDto, product, supplier)).thenReturn(mappedEntity);
        when(batchRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(batchMapper.toDto(savedEntity)).thenReturn(expectedDto);

        BatchDto result = batchService.createBatch(inputDto);

        assertEquals(expectedDto, result);
        verify(productRepository).findById(productId);
        verify(supplierRepository).findById(supplierId);
    }

    @Test
    void createBatch_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {
        Long invalidProductId = 99L;
        BatchDto inputDto = new BatchDto(
                null, invalidProductId, "BATCH123", LocalDate.now(), null);

        when(productRepository.findById(invalidProductId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> batchService.createBatch(inputDto)
        );

        assertEquals("Product not found with id: 99", exception.getMessage());

        verifyNoInteractions(supplierRepository, batchMapper, batchRepository);
    }

    @Test
    void updateBatch_ShouldUpdateBatchAndIgnoreProduct_WhenBatchAndSupplierExist() {
        Long batchId = 1L;
        Long originalProductId = 10L;
        Long newSupplierId = 20L;

        Product originalProduct = new Product();
        originalProduct.setId(originalProductId);
        originalProduct.setProductName("Original Product");

        Batch existingBatch = new Batch();
        existingBatch.setId(batchId);
        existingBatch.setProduct(originalProduct);
        existingBatch.setLotNumber("LOT_OLD");
        existingBatch.setExpiryDate(LocalDate.now().plusDays(10));

        Supplier newSupplier = new Supplier();
        newSupplier.setId(newSupplierId);

        BatchDto updateDto = new BatchDto(
                batchId,999L,"LOT_NEW", LocalDate.now().plusDays(60), newSupplierId);

        BatchDto expectedDto = new BatchDto(
                batchId,originalProductId, "LOT_NEW", LocalDate.now().plusDays(60), newSupplierId);

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(existingBatch));
        when(supplierRepository.findById(newSupplierId)).thenReturn(Optional.of(newSupplier));

        doAnswer(invocation -> {
            existingBatch.setLotNumber(updateDto.lotNumber());
            existingBatch.setExpiryDate(updateDto.expiryDate());
            existingBatch.setSupplier(newSupplier);
            return null;
        }).when(batchMapper).updateEntity(updateDto, newSupplier, existingBatch);

        when(batchMapper.toDto(existingBatch)).thenReturn(expectedDto);

        BatchDto result = batchService.updateBatch(batchId, updateDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        assertEquals(originalProductId, existingBatch.getProduct().getId());
        assertEquals("Original Product", existingBatch.getProduct().getProductName());

        verifyNoInteractions(productRepository);

        verify(batchRepository).findById(batchId);
        verify(supplierRepository).findById(newSupplierId);
        verify(batchMapper).updateEntity(updateDto, newSupplier, existingBatch);
        verify(batchMapper).toDto(existingBatch);
    }

    @Test
    void updateBatch_ShouldSetSupplierToNull_WhenSupplierIdIsNull() {
        Long batchId = 1L;
        Product product = new Product();
        product.setId(10L);

        Batch existingBatch = new Batch();
        existingBatch.setId(batchId);
        existingBatch.setProduct(product);

        BatchDto updateDto = new BatchDto(
                batchId, 10L, "LOT_NEW", LocalDate.now().plusDays(30), null);
        BatchDto expectedDto = new BatchDto(
                batchId, 10L, "LOT_NEW", LocalDate.now().plusDays(30), null);

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(existingBatch));
        when(batchMapper.toDto(existingBatch)).thenReturn(expectedDto);

        BatchDto result = batchService.updateBatch(batchId, updateDto);

        assertEquals(expectedDto, result);

        verifyNoInteractions(supplierRepository, productRepository);
        verify(batchMapper).updateEntity(updateDto, null, existingBatch);
    }

    @Test
    void updateBatch_ShouldThrowResourceNotFoundException_WhenBatchNotFound() {
        Long nonExistentBatchId = 999L;
        BatchDto updateDto = new BatchDto(
                nonExistentBatchId,10L,"LOT",LocalDate.now(),5L);

        when(batchRepository.findById(nonExistentBatchId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> batchService.updateBatch(nonExistentBatchId, updateDto)
        );

        assertEquals("Batch not found with id: 999", exception.getMessage());
        verifyNoInteractions(supplierRepository, productRepository, batchMapper);
    }

    @Test
    void updateBatch_ShouldThrowResourceNotFoundException_WhenSupplierNotFound() {
        Long batchId = 1L;
        Long nonExistentSupplierId = 888L;

        Batch existingBatch = new Batch();
        existingBatch.setId(batchId);

        BatchDto updateDto = new BatchDto(
                batchId, 10L, "LOT", LocalDate.now(), nonExistentSupplierId);

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(existingBatch));
        when(supplierRepository.findById(nonExistentSupplierId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> batchService.updateBatch(batchId, updateDto)
        );

        assertEquals("Supplier not found with id: 888", exception.getMessage());
        verifyNoInteractions(productRepository);
        verify(batchMapper, never()).updateEntity(any(), any(), any());
    }

    @Test
    void getBatchById_ShouldReturnBatchDto_WhenBatchExists() {
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setId(batchId);

        BatchDto expectedDto = new BatchDto(
                batchId, 10L, "LOT123", LocalDate.now().plusDays(30), 5L);

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(batchMapper.toDto(batch)).thenReturn(expectedDto);

        BatchDto result = batchService.getBatchById(batchId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(batchRepository).findById(batchId);
        verify(batchMapper).toDto(batch);
    }

    @Test
    void getBatchById_ShouldThrowResourceNotFoundException_WhenBatchDoesNotExist() {
        Long nonExistentId = 999L;
        when(batchRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> batchService.getBatchById(nonExistentId)
        );

        assertEquals("Batch not found with id: 999", exception.getMessage());
        verify(batchRepository).findById(nonExistentId);
        verifyNoInteractions(batchMapper);
    }



    @Test
    void getBatchesByProductId_ShouldReturnListOfDtos_WhenBatchesExist() {
        Long productId = 10L;
        Batch batch1 = new Batch();
        batch1.setId(1L);
        Batch batch2 = new Batch();
        batch2.setId(2L);

        List<Batch> entityList = List.of(batch1, batch2);

        BatchDto dto1 = new BatchDto(1L, productId, "LOT_1", LocalDate.now().plusDays(10), 1L);
        BatchDto dto2 = new BatchDto(2L, productId, "LOT_2", LocalDate.now().plusDays(20), 1L);
        List<BatchDto> expectedDtos = List.of(dto1, dto2);

        when(batchRepository.findAllByProductId(productId)).thenReturn(entityList);
        when(batchMapper.toDtoList(entityList)).thenReturn(expectedDtos);

        List<BatchDto> result = batchService.getBatchesByProductId(productId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);

        verify(batchRepository).findAllByProductId(productId);
        verify(batchMapper).toDtoList(entityList);
    }

    @Test
    void getBatchesByProductId_ShouldReturnEmptyList_WhenNoBatchesExist() {
        Long productId = 99L;
        when(batchRepository.findAllByProductId(productId)).thenReturn(Collections.emptyList());
        when(batchMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<BatchDto> result = batchService.getBatchesByProductId(productId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(batchRepository).findAllByProductId(productId);
        verify(batchMapper).toDtoList(Collections.emptyList());
    }


    @Test
    void getBatchesByExpiryDateBefore_ShouldReturnListOfDtos_WhenExpiringBatchesExist() {
        LocalDate thresholdDate = LocalDate.now().plusDays(7);

        Batch batch = new Batch();
        batch.setId(1L);
        List<Batch> entityList = List.of(batch);

        BatchDto dto = new BatchDto(1L, 10L, "EXP_LOT", LocalDate.now().plusDays(2), 2L);
        List<BatchDto> expectedDtos = List.of(dto);

        when(batchRepository.findAllByExpiryDateBefore(thresholdDate)).thenReturn(entityList);
        when(batchMapper.toDtoList(entityList)).thenReturn(expectedDtos);

        List<BatchDto> result = batchService.getBatchesByExpiryDateBefore(thresholdDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("EXP_LOT", result.get(0).lotNumber());

        verify(batchRepository).findAllByExpiryDateBefore(thresholdDate);
        verify(batchMapper).toDtoList(entityList);
    }
}