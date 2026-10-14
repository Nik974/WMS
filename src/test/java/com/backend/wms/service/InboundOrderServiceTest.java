package com.backend.wms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.wms.dto.CreateInboundOrderDto;
import com.backend.wms.dto.InboundOrderDto;
import com.backend.wms.entity.InboundOrder;
import com.backend.wms.entity.Supplier;
import com.backend.wms.entity.Warehouse;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.InboundOrderMapper;
import com.backend.wms.repository.InboundOrderRepository;
import com.backend.wms.repository.SupplierRepository;
import com.backend.wms.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboundOrderServiceTest {

    @InjectMocks
    private InboundOrderService inboundOrderService;

    @Mock
    private InboundOrderRepository inboundOrderRepository;

    @Mock
    private InboundOrderMapper inboundOrderMapper;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Test
    void getAllInboundOrders_ShouldReturnListOfDtos() {
        InboundOrder order = new InboundOrder();
        List<InboundOrder> orders = List.of(order);
        List<InboundOrderDto> expectedDtos = List.of(
                new InboundOrderDto(1L, 1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of())
        );

        when(inboundOrderRepository.findAll()).thenReturn(orders);
        when(inboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<InboundOrderDto> result = inboundOrderService.getAllInboundOrders();

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(inboundOrderRepository).findAll();
        verify(inboundOrderMapper).toDtoList(orders);
    }

    @Test
    void getInboundOrdersBySupplierId_ShouldReturnListOfDtos_WhenSupplierExists() {
        Long supplierId = 1L;
        InboundOrder order = new InboundOrder();
        List<InboundOrder> orders = List.of(order);
        List<InboundOrderDto> expectedDtos = List.of(
                new InboundOrderDto(1L, 1L, supplierId, "Supplier A", "NEW", LocalDateTime.now(), List.of())
        );

        when(supplierRepository.existsById(supplierId)).thenReturn(true);
        when(inboundOrderRepository.findAllBySupplierId(supplierId)).thenReturn(orders);
        when(inboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<InboundOrderDto> result = inboundOrderService.getInboundOrdersBySupplierId(supplierId);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(supplierRepository).existsById(supplierId);
        verify(inboundOrderRepository).findAllBySupplierId(supplierId);
    }

    @Test
    void getInboundOrdersBySupplierId_ShouldThrowException_WhenSupplierNotFound() {
        Long supplierId = 1L;
        when(supplierRepository.existsById(supplierId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.getInboundOrdersBySupplierId(supplierId));
        verify(supplierRepository).existsById(supplierId);
        verifyNoInteractions(inboundOrderRepository);
    }

    @Test
    void getInboundOrdersByWarehouseId_ShouldReturnListOfDtos_WhenWarehouseExists() {
        Long warehouseId = 1L;
        InboundOrder order = new InboundOrder();
        List<InboundOrder> orders = List.of(order);
        List<InboundOrderDto> expectedDtos = List.of(
                new InboundOrderDto(1L, warehouseId, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of())
        );

        when(warehouseRepository.existsById(warehouseId)).thenReturn(true);
        when(inboundOrderRepository.findAllByWarehouseId(warehouseId)).thenReturn(orders);
        when(inboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<InboundOrderDto> result = inboundOrderService.getInboundOrdersByWarehouseId(warehouseId);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(warehouseRepository).existsById(warehouseId);
        verify(inboundOrderRepository).findAllByWarehouseId(warehouseId);
    }

    @Test
    void getInboundOrdersByWarehouseId_ShouldThrowException_WhenWarehouseNotFound() {
        Long warehouseId = 1L;
        when(warehouseRepository.existsById(warehouseId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.getInboundOrdersByWarehouseId(warehouseId));
        verify(warehouseRepository).existsById(warehouseId);
        verifyNoInteractions(inboundOrderRepository);
    }

    @Test
    void getInboundOrdersByStatus_ShouldReturnListOfDtos() {
        String status = "NEW";
        InboundOrder order = new InboundOrder();
        List<InboundOrder> orders = List.of(order);
        List<InboundOrderDto> expectedDtos = List.of(
                new InboundOrderDto(1L, 1L, 1L, "Supplier A", status, LocalDateTime.now(), List.of())
        );

        when(inboundOrderRepository.findAllByStatus(status)).thenReturn(orders);
        when(inboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<InboundOrderDto> result = inboundOrderService.getInboundOrdersByStatus(status);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(inboundOrderRepository).findAllByStatus(status);
    }

    @Test
    void getInboundOrdersByCreatedBetween_ShouldReturnListOfDtos() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        InboundOrder order = new InboundOrder();
        List<InboundOrder> orders = List.of(order);
        List<InboundOrderDto> expectedDtos = List.of(
                new InboundOrderDto(1L, 1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of())
        );

        when(inboundOrderRepository.findTopByCreatedAtBetween(start, end)).thenReturn(orders);
        when(inboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<InboundOrderDto> result = inboundOrderService.getInboundOrdersByCreatedBetween(start, end);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(inboundOrderRepository).findTopByCreatedAtBetween(start, end);
    }

    @Test
    void createInboundOrder_ShouldSaveAndReturnDto_WhenValid() {
        CreateInboundOrderDto inputDto = new CreateInboundOrderDto(1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of());
        InboundOrder entity = new InboundOrder();
        InboundOrder savedEntity = new InboundOrder();
        savedEntity.setId(1L);
        InboundOrderDto expectedDto = new InboundOrderDto(1L, 1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of());

        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(inboundOrderMapper.toEntity(inputDto)).thenReturn(entity);
        when(inboundOrderRepository.save(entity)).thenReturn(savedEntity);
        when(inboundOrderMapper.toDto(savedEntity)).thenReturn(expectedDto);

        InboundOrderDto result = inboundOrderService.createInboundOrder(inputDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(warehouseRepository).existsById(1L);
        verify(supplierRepository).existsById(1L);
        verify(inboundOrderRepository).save(entity);
    }

    @Test
    void createInboundOrder_ShouldThrowException_WhenWarehouseNotFound() {
        CreateInboundOrderDto inputDto = new CreateInboundOrderDto(1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of());

        when(warehouseRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.createInboundOrder(inputDto));
        verify(warehouseRepository).existsById(1L);
        verifyNoInteractions(supplierRepository, inboundOrderRepository, inboundOrderMapper);
    }

    @Test
    void createInboundOrder_ShouldThrowException_WhenSupplierNotFound() {
        CreateInboundOrderDto inputDto = new CreateInboundOrderDto(1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of());

        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(supplierRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.createInboundOrder(inputDto));
        verify(warehouseRepository).existsById(1L);
        verify(supplierRepository).existsById(1L);
        verifyNoInteractions(inboundOrderRepository, inboundOrderMapper);
    }

    @Test
    void editInboundOrder_ShouldUpdateAndReturnDto_WhenValid() {
        Long orderId = 1L;
        InboundOrderDto updateDto = new InboundOrderDto(orderId, 2L, 2L, "Supplier B", "PROCESSING", LocalDateTime.now(), List.of());
        InboundOrder existingOrder = new InboundOrder();
        existingOrder.setId(orderId);

        when(inboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(warehouseRepository.existsById(2L)).thenReturn(true);
        when(supplierRepository.existsById(2L)).thenReturn(true);
        when(warehouseRepository.getReferenceById(2L)).thenReturn(new Warehouse());
        when(supplierRepository.getReferenceById(2L)).thenReturn(new Supplier());
        when(inboundOrderRepository.save(existingOrder)).thenReturn(existingOrder);
        when(inboundOrderMapper.toDto(existingOrder)).thenReturn(updateDto);

        InboundOrderDto result = inboundOrderService.editInboundOrder(orderId, updateDto);

        assertNotNull(result);
        assertEquals(updateDto, result);
        verify(inboundOrderRepository).findById(orderId);
        verify(warehouseRepository).existsById(2L);
        verify(supplierRepository).existsById(2L);
        verify(inboundOrderRepository).save(existingOrder);
    }

    @Test
    void editInboundOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;
        InboundOrderDto updateDto = new InboundOrderDto(orderId, 1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of());

        when(inboundOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.editInboundOrder(orderId, updateDto));
        verify(inboundOrderRepository).findById(orderId);
        verifyNoInteractions(warehouseRepository, supplierRepository);
    }

    @Test
    void editInboundOrder_ShouldThrowException_WhenWarehouseNotFound() {
        Long orderId = 1L;
        InboundOrderDto updateDto = new InboundOrderDto(orderId, 1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of());
        InboundOrder existingOrder = new InboundOrder();

        when(inboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(warehouseRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.editInboundOrder(orderId, updateDto));
        verify(inboundOrderRepository).findById(orderId);
        verify(warehouseRepository).existsById(1L);
        verifyNoInteractions(supplierRepository);
    }

    @Test
    void editInboundOrder_ShouldThrowException_WhenSupplierNotFound() {
        Long orderId = 1L;
        InboundOrderDto updateDto = new InboundOrderDto(orderId, 1L, 1L, "Supplier A", "NEW", LocalDateTime.now(), List.of());
        InboundOrder existingOrder = new InboundOrder();

        when(inboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(supplierRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.editInboundOrder(orderId, updateDto));
        verify(inboundOrderRepository).findById(orderId);
        verify(warehouseRepository).existsById(1L);
        verify(supplierRepository).existsById(1L);
    }
}