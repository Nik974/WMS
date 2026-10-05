package com.backend.wms.service;

import com.backend.wms.dto.WarehouseDto;
import com.backend.wms.entity.Warehouse;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.WarehouseMapper;
import com.backend.wms.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Transactional
    public WarehouseDto createWarehouse(WarehouseDto warehouseDto) {
        String trimmedName = warehouseDto.warehouseName().trim();
        if (warehouseRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ResourceAlreadyExistsException("Warehouse with name " + trimmedName + " already exists.");
        }
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDto);
        warehouse.setName(trimmedName);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(savedWarehouse);
    }

    @Transactional
    public WarehouseDto updateWarehouse(Long id, WarehouseDto warehouseDto) {

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        String trimmedName = warehouseDto.warehouseName().trim();

        if (!warehouse.getName().equalsIgnoreCase(trimmedName)
                && warehouseRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ResourceAlreadyExistsException("Warehouse with name " + trimmedName + " already exists.");
        }

        warehouseMapper.updateEntityFromDto(warehouseDto, warehouse);
        warehouse.setName(trimmedName);
        return warehouseMapper.toDto(warehouse);
    }

    public WarehouseDto getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .map(warehouseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
    }

    public List<WarehouseDto> getAllWarehouses() {
        return warehouseMapper.toDtoList(warehouseRepository.findAll());
    }

    public List<WarehouseDto> searchWarehousesByName(String name) {
        return warehouseMapper.toDtoList(warehouseRepository.findByNameContainingIgnoreCase(name));
    }

    public List<WarehouseDto> searchWarehousesByAddress(String address) {
        return warehouseMapper.toDtoList(warehouseRepository.findByAddressContainingIgnoreCase(address));
    }

}
