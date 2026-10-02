package com.backend.wms.service;

import com.backend.wms.dto.SupplierDto;
import com.backend.wms.dto.UpdateSupplierDto;
import com.backend.wms.entity.Supplier;
import com.backend.wms.exception.ResourceAlreadyInUse;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.SupplierMapper;
import com.backend.wms.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;


import java.util.List;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @InjectMocks
    private SupplierService supplierService;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @Test
    void addSupplier_ShouldSaveAndReturnDto_WhenSupplierIsUnique() {
        Supplier supplier = new Supplier();
        supplier.setSupplierName("Test Supplier");
        supplier.setTaxId("123456789");
        supplier.setId(1L);

        SupplierDto supplierDto = new SupplierDto(1L, "Test Supplier", "123456789");

        when(supplierRepository.existsByTaxId("123456789")).thenReturn(false);
        when(supplierMapper.toEntity(supplierDto)).thenReturn(supplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toDto(supplier)).thenReturn(supplierDto);

        SupplierDto result = supplierService.addSupplier(supplierDto);
        assertEquals(supplierDto, result);
    }

    @Test
    void addSupplier_ShouldThrowException_WhenSupplierWithSameTaxIdExists() {
        SupplierDto supplierDto = new SupplierDto(null, "Test Supplier", "123456789");

        when(supplierRepository.existsByTaxId("123456789")).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            supplierService.addSupplier(supplierDto);
        });

        String expectedMessage = "Supplier with tax ID 123456789 already exists.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateSupplier_ShouldUpdateAndReturnDto_WhenSupplierExists() {
        Supplier existingSupplier = new Supplier();
        existingSupplier.setId(1L);
        existingSupplier.setSupplierName("Old Supplier");
        existingSupplier.setTaxId("987654321");

        UpdateSupplierDto updatedSupplierDto = new UpdateSupplierDto( "Updated Supplier", "123456789");
        SupplierDto expectedDto = new SupplierDto(1L, "Updated Supplier", "123456789");
        when(supplierRepository.findById(1L)).thenReturn(of(existingSupplier));
        when(supplierRepository.existsByTaxIdAndIdNot("123456789", 1L)).thenReturn(false);
        when(supplierMapper.toDto(existingSupplier)).thenReturn(expectedDto);

        SupplierDto result = supplierService.updateSupplier(1L, updatedSupplierDto);
        assertEquals(expectedDto, result);
    }

    @Test
    void updateSupplier_ShouldThrowException_WhenSupplierDoesNotExist() {
        UpdateSupplierDto updatedSupplierDto = new UpdateSupplierDto("Updated Supplier", "123456789");

        when(supplierRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            supplierService.updateSupplier(1L, updatedSupplierDto);
        });

        String expectedMessage = "Supplier not found with id: 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateSupplier_ShouldThrowException_WhenTaxIdAlreadyExists() {
        Supplier existingSupplier = new Supplier();
        existingSupplier.setId(1L);
        existingSupplier.setSupplierName("Old Supplier");
        existingSupplier.setTaxId("987654321");

        UpdateSupplierDto updatedSupplierDto = new UpdateSupplierDto("Updated Supplier", "123456789");

        when(supplierRepository.findById(1L)).thenReturn(of(existingSupplier));
        when(supplierRepository.existsByTaxIdAndIdNot("123456789", 1L)).thenReturn(true);

        Exception exception = assertThrows(ResourceAlreadyInUse.class, () -> {
            supplierService.updateSupplier(1L, updatedSupplierDto);
        });

        String expectedMessage = "Tax ID '123456789' is already in use by another supplier";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getSupplierByTaxId_ShouldReturnDto_WhenSupplierExists() {
        Supplier supplier = new Supplier();
        supplier.setSupplierName("Test Supplier");
        supplier.setTaxId("123456789");
        supplier.setId(1L);

        SupplierDto supplierDto = new SupplierDto(1L, "Test Supplier", "123456789");

        when(supplierRepository.findByTaxId("123456789")).thenReturn(of(supplier));
        when(supplierMapper.toDto(supplier)).thenReturn(supplierDto);

        SupplierDto result = supplierService.getSupplierByTaxId("123456789");
        assertEquals(supplierDto, result);
    }

    @Test
    void getSupplierByTaxId_ShouldThrowException_WhenSupplierDoesNotExist() {
        when(supplierRepository.findByTaxId("123456789")).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            supplierService.getSupplierByTaxId("123456789");
        });

        String expectedMessage = "Supplier not found with tax ID: 123456789";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void searchSuppliers_ShouldReturnListOfDtos_WhenSuppliersExist() {
        Supplier supplier1 = new Supplier();
        supplier1.setSupplierName("Supplier One");
        supplier1.setTaxId("111111111");
        supplier1.setId(1L);

        Supplier supplier2 = new Supplier();
        supplier2.setSupplierName("Supplier Two");
        supplier2.setTaxId("222222222");
        supplier2.setId(2L);

        SupplierDto supplierDto1 = new SupplierDto(1L, "Supplier One", "111111111");
        SupplierDto supplierDto2 = new SupplierDto(2L, "Supplier Two", "222222222");

        when(supplierRepository.findAllBySupplierNameContainingIgnoreCase(eq("Supplier One"), any(Sort.class))).thenReturn(List.of(supplier1, supplier2));
        when(supplierMapper.toDto(supplier1)).thenReturn(supplierDto1);
        when(supplierMapper.toDto(supplier2)).thenReturn(supplierDto2);

        java.util.List<SupplierDto> result = supplierService.searchSuppliersByName("Supplier One");

        assertEquals(2, result.size());
        assertTrue(result.contains(supplierDto1));
        assertTrue(result.contains(supplierDto2));
    }
    @Test
    void searchSuppliersByName_ShouldReturnEmptyList_WhenNoSuppliersFound() {
        String searchName = "NonExistentSupplier";

        when(supplierRepository.findAllBySupplierNameContainingIgnoreCase(
                eq(searchName),
                any(Sort.class)
        )).thenReturn(List.of());

        List<SupplierDto> result = supplierService.searchSuppliersByName(searchName);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verifyNoInteractions(supplierMapper);
    }
}