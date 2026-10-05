package com.backend.wms.service;

import com.backend.wms.dto.WarehouseDto;
import com.backend.wms.entity.Warehouse;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.WarehouseMapper;
import com.backend.wms.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseService warehouseService;

    @Mock
    private WarehouseMapper warehouseMapper;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Test
    void createWarehouse_ShouldThrowException_WhenWarehouseNameAlreadyExists() {

        Warehouse warehouse = new Warehouse();
        warehouse.setName("Existing Warehouse");
        warehouse.setAddress("123 Main St");
        warehouse.setId(1L);

        WarehouseDto warehouseDto = new WarehouseDto(null, "Existing Warehouse", "123 Main St");

        when(warehouseRepository.existsByNameIgnoreCase("Existing Warehouse")).thenReturn(true);

        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            warehouseService.createWarehouse(warehouseDto);
        });

        String expectedMessage = "Warehouse with name Existing Warehouse already exists.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createWarehouse_ShouldCreateWarehouse_WhenWarehouseNameDoesNotExist() {
        WarehouseDto warehouseDto = new WarehouseDto(null, "New Warehouse", "456 Elm St");
        Warehouse warehouse = new Warehouse();
        warehouse.setName("New Warehouse");
        warehouse.setAddress("456 Elm St");
        warehouse.setId(2L);

        when(warehouseRepository.existsByNameIgnoreCase("New Warehouse")).thenReturn(false);
        when(warehouseMapper.toEntity(warehouseDto)).thenReturn(warehouse);
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        when(warehouseMapper.toDto(warehouse)).thenReturn(new WarehouseDto(2L, "New Warehouse", "456 Elm St"));

        WarehouseDto createdWarehouse = warehouseService.createWarehouse(warehouseDto);

        assertNotNull(createdWarehouse);
        assertEquals("New Warehouse", createdWarehouse.warehouseName());
        assertEquals("456 Elm St", createdWarehouse.warehouseAddress());
    }

    @Test
    void updateWarehouse_ShouldThrowException_WhenWarehouseNameAlreadyExists() {
        Long warehouseId = 1L;

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setId(warehouseId);
        existingWarehouse.setName("Old Warehouse Name");
        existingWarehouse.setAddress("123 Main St");

        WarehouseDto warehouseDto = new WarehouseDto(null, "New Warehouse Name", "456 Elm St");

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
        when(warehouseRepository.existsByNameIgnoreCase("New Warehouse Name")).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> warehouseService.updateWarehouse(warehouseId, warehouseDto)
        );

        String expectedMessage = "Warehouse with name New Warehouse Name already exists.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateWarehouse_ShouldUpdateWarehouse_WhenWarehouseNameDoesNotExist() {
        Long warehouseId = 1L;

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setId(warehouseId);
        existingWarehouse.setName("Old Warehouse Name");
        existingWarehouse.setAddress("123 Main St");

        WarehouseDto warehouseDto = new WarehouseDto(null, "New Warehouse Name", "456 Elm St");

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
        when(warehouseRepository.existsByNameIgnoreCase("New Warehouse Name")).thenReturn(false);
        when(warehouseMapper.toDto(existingWarehouse)).thenReturn(new WarehouseDto(warehouseId, "New Warehouse Name", "456 Elm St"));

        WarehouseDto updatedWarehouse = warehouseService.updateWarehouse(warehouseId, warehouseDto);

        assertNotNull(updatedWarehouse);
        assertEquals("New Warehouse Name", updatedWarehouse.warehouseName());
        assertEquals("456 Elm St", updatedWarehouse.warehouseAddress());
    }

    @Test
    void updateWarehouse_ShouldThrowException_WhenWarehouseNotFound() {
        Long warehouseId = 1L;

        WarehouseDto warehouseDto = new WarehouseDto(null, "New Warehouse Name", "456 Elm St");

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            warehouseService.updateWarehouse(warehouseId, warehouseDto);
        });

        String expectedMessage = "Warehouse not found with id: " + warehouseId;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getWarehouseById_ShouldReturnWarehouse_WhenWarehouseExists() {
        Long warehouseId = 1L;

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setId(warehouseId);
        existingWarehouse.setName("Existing Warehouse");
        existingWarehouse.setAddress("123 Main St");

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
        when(warehouseMapper.toDto(existingWarehouse)).thenReturn(new WarehouseDto(warehouseId, "Existing Warehouse", "123 Main St"));

        WarehouseDto warehouseDto = warehouseService.getWarehouseById(warehouseId);

        assertNotNull(warehouseDto);
        assertEquals("Existing Warehouse", warehouseDto.warehouseName());
        assertEquals("123 Main St", warehouseDto.warehouseAddress());
    }

    @Test
    void updateWarehouse_ShouldUpdateWarehouse_WhenNameIsUnchanged() {
        Long warehouseId = 1L;
        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setId(warehouseId);
        existingWarehouse.setName("Same Warehouse Name");
        existingWarehouse.setAddress("Old Address");

        WarehouseDto warehouseDto = new WarehouseDto(null, "Same Warehouse Name", "New Address");

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
        when(warehouseMapper.toDto(existingWarehouse)).thenReturn(new WarehouseDto(warehouseId, "Same Warehouse Name", "New Address"));

        WarehouseDto updatedWarehouse = warehouseService.updateWarehouse(warehouseId, warehouseDto);

        assertNotNull(updatedWarehouse);
        assertEquals("New Address", updatedWarehouse.warehouseAddress());
        verify(warehouseRepository, never()).existsByNameIgnoreCase("Same Warehouse Name");
    }

    @Test
    void getWarehouseById_ShouldThrowException_WhenWarehouseNotFound() {
        Long warehouseId = 99L;
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> warehouseService.getWarehouseById(warehouseId)
        );

        assertEquals("Warehouse not found with id: 99", exception.getMessage());
    }

    @Test
    void getAllWarehouses_ShouldReturnListOfWarehouses() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse 1");

        WarehouseDto warehouseDto = new WarehouseDto(1L, "Warehouse 1", "Address 1");

        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));
        when(warehouseMapper.toDtoList(List.of(warehouse))).thenReturn(List.of(warehouseDto));

        List<WarehouseDto> result = warehouseService.getAllWarehouses();

        assertEquals(1, result.size());
        assertEquals("Warehouse 1", result.get(0).warehouseName());
    }

    @Test
    void searchWarehousesByName_ShouldReturnMatchingWarehouses() {
        String searchName = "Central";
        Warehouse warehouse = new Warehouse();
        WarehouseDto warehouseDto = new WarehouseDto(1L, "Central Warehouse", "Main St");

        when(warehouseRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(List.of(warehouse));
        when(warehouseMapper.toDtoList(List.of(warehouse))).thenReturn(List.of(warehouseDto));

        List<WarehouseDto> result = warehouseService.searchWarehousesByName(searchName);

        assertEquals(1, result.size());
        assertEquals("Central Warehouse", result.get(0).warehouseName());
    }

    @Test
    void searchWarehousesByAddress_ShouldReturnMatchingWarehouses() {
        String searchAddress = "Elm St";
        Warehouse warehouse = new Warehouse();
        WarehouseDto warehouseDto = new WarehouseDto(1L, "Warehouse 1", "456 Elm St");

        when(warehouseRepository.findByAddressContainingIgnoreCase(searchAddress)).thenReturn(List.of(warehouse));
        when(warehouseMapper.toDtoList(List.of(warehouse))).thenReturn(List.of(warehouseDto));

        List<WarehouseDto> result = warehouseService.searchWarehousesByAddress(searchAddress);

        assertEquals(1, result.size());
        assertEquals("456 Elm St", result.get(0).warehouseAddress());
    }

    @Test
    void createWarehouse_ShouldTrimWarehouseName() {
        WarehouseDto warehouseDto = new WarehouseDto(null, "  Spacious Warehouse  ", "123 Main St");
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Spacious Warehouse");

        when(warehouseRepository.existsByNameIgnoreCase("Spacious Warehouse")).thenReturn(false);
        when(warehouseMapper.toEntity(warehouseDto)).thenReturn(warehouse);
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        when(warehouseMapper.toDto(warehouse)).thenReturn(new WarehouseDto(1L, "Spacious Warehouse", "123 Main St"));

        warehouseService.createWarehouse(warehouseDto);


        verify(warehouseRepository).existsByNameIgnoreCase("Spacious Warehouse");
    }
}