package com.backend.wms.service;

import com.backend.wms.dto.SupplierDto;
import com.backend.wms.dto.UpdateSupplierDto;
import com.backend.wms.entity.Supplier;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.SupplierMapper;
import com.backend.wms.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Transactional
    public SupplierDto addSupplier(SupplierDto dto) {
        String taxId = normalize(dto.taxId());
        if (taxId != null && supplierRepository.existsByTaxId(taxId)) {
            throw new ResourceAlreadyExistsException(
                    "Supplier with tax ID " + taxId + " already exists.");
        }

        Supplier supplier = supplierMapper.toEntity(dto);
        supplier.setSupplierName(supplier.getSupplierName().trim());
        supplier.setTaxId(taxId);

        return supplierMapper.toDto(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierDto updateSupplier(Long supplierId, UpdateSupplierDto dto) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + supplierId));

        String taxId = normalize(dto.taxId());
        if (taxId != null && supplierRepository.existsByTaxIdAndIdNot(taxId, supplierId)) {
            throw new ResourceAlreadyExistsException(
                    "Tax ID '" + taxId + "' is already in use by another supplier");
        }

        supplierMapper.updateEntityFromDto(dto, supplier);
        supplier.setTaxId(taxId);
        if (supplier.getSupplierName() != null) {
            supplier.setSupplierName(supplier.getSupplierName().trim());
        }

        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    public SupplierDto getSupplierByTaxId(String taxId) {
        String normalized = normalize(taxId);
        if (normalized == null) {
            throw new ResourceNotFoundException("Supplier not found with empty tax ID");
        }
        return supplierRepository.findByTaxId(normalized)
                .map(supplierMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with tax ID: " + normalized));
    }

    public List<SupplierDto> searchSuppliersByName(String name) {
        String query = name == null ? "" : name.trim();
        return supplierMapper.toDtoList(
                supplierRepository.findAllBySupplierNameContainingIgnoreCase(
                        query, Sort.by(Sort.Direction.ASC, "supplierName")));
    }

    public List<SupplierDto> getAllSuppliers() {
        return supplierMapper.toDtoList(
                supplierRepository.findAll(Sort.by(Sort.Direction.ASC, "supplierName")));
    }

    private static String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
