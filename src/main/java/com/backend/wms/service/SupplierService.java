package com.backend.wms.service;

import com.backend.wms.dto.SupplierDto;
import com.backend.wms.dto.UpdateSupplierDto;
import com.backend.wms.entity.Supplier;
import com.backend.wms.exception.ResourceAlreadyInUse;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.SupplierMapper;
import com.backend.wms.repository.SupplierRepository;
import jakarta.validation.Valid;
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
    public SupplierDto addSupplier(@Valid SupplierDto supplierDto) {
        if(supplierRepository.existsByTaxId(supplierDto.taxId())) {
            throw new ResourceAlreadyExistsException("Supplier with tax ID " + supplierDto.taxId() + " already exists.");
        }
        Supplier supplier = supplierMapper.toEntity(supplierDto);
        supplier.setSupplierName(supplier.getSupplierName().trim());
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }
    @Transactional
    public SupplierDto updateSupplier(Long supplierId, @Valid UpdateSupplierDto supplierDto) {

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));

        if (supplierDto.taxId() != null && !supplierDto.taxId().isBlank()) {
            String trimmedTaxId = supplierDto.taxId().trim();

            if (supplierRepository.existsByTaxIdAndIdNot(trimmedTaxId, supplierId)) {
                throw new ResourceAlreadyInUse("Tax ID '" + trimmedTaxId + "' is already in use by another supplier");
            }
            supplier.setTaxId(trimmedTaxId);
        } else {
            supplier.setTaxId(null);
        }

        supplierMapper.updateEntityFromDto(supplierDto, supplier);
        return supplierMapper.toDto(supplier);
    }

    public SupplierDto getSupplierByTaxId(String taxId) {
        Supplier supplier = supplierRepository.findByTaxId(taxId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with tax ID: " + taxId));
        return supplierMapper.toDto(supplier);
    }

    public List<SupplierDto> searchSuppliersByName(String supplierName) {
        return supplierRepository.findAllBySupplierNameContainingIgnoreCase(
                        supplierName,
                        Sort.by(Sort.Direction.ASC, "supplierName"))
                .stream()
                .map(supplierMapper::toDto)
                .toList();
    }
}
