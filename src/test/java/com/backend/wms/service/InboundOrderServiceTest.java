package com.backend.wms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.wms.dto.CreateInboundOrderDto;
import com.backend.wms.dto.InboundItemDto;
import com.backend.wms.dto.InboundOrderDto;
import com.backend.wms.entity.*;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.InboundOrderMapper;
import com.backend.wms.repository.InboundOrderRepository;
import com.backend.wms.repository.ProductRepository;
import com.backend.wms.repository.SupplierRepository;
import com.backend.wms.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Mock
    private ProductRepository productRepository;

    @Test
    void getAllInboundOrders_ShouldReturnListOfDtos() {
        InboundOrder order = new InboundOrder();
        List<InboundOrder> orders = List.of(order);
        List<InboundOrderDto> expectedDtos = List.of(
                new InboundOrderDto(1L, 1L, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of())
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
                new InboundOrderDto(1L, 1L, supplierId, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of())
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
                new InboundOrderDto(1L, warehouseId, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of())
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
        InboundStatus status = InboundStatus.NEW;
        InboundOrder order = new InboundOrder();
        List<InboundOrder> orders = List.of(order);
        List<InboundOrderDto> expectedDtos = List.of(
                new InboundOrderDto(1L, 1L, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of())
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
        List<InboundOrder> orders = List.of(new InboundOrder());
        List<InboundOrderDto> expectedDtos = List.of(
                new InboundOrderDto(1L, 1L, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of()));

        when(inboundOrderRepository.findAllByCreatedAtBetween(start, end)).thenReturn(orders);
        when(inboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<InboundOrderDto> result = inboundOrderService.getInboundOrdersByCreatedBetween(start, end);

        assertEquals(expectedDtos, result);
        verify(inboundOrderRepository).findAllByCreatedAtBetween(start, end);
    }

    @Test
    void createInboundOrder_ShouldSaveAndReturnDto_WhenValid() {
        CreateInboundOrderDto dto = new CreateInboundOrderDto(
                1L, 2L, null, null, null,
                List.of(new InboundItemDto(null, null, 10L, 3, null)));
        InboundOrderDto expectedDto = new InboundOrderDto(
                1L, 1L, 2L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of());

        Warehouse warehouse = new Warehouse();
        Supplier supplier = new Supplier();
        Product product = new Product();
        product.setId(10L);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));
        when(productRepository.findAllById(Set.of(10L))).thenReturn(List.of(product));
        when(inboundOrderRepository.save(any(InboundOrder.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(inboundOrderMapper.toDto(any(InboundOrder.class))).thenReturn(expectedDto);

        InboundOrderDto result = inboundOrderService.createInboundOrder(dto);

        assertEquals(expectedDto, result);
        ArgumentCaptor<InboundOrder> captor = ArgumentCaptor.forClass(InboundOrder.class);
        verify(inboundOrderRepository).save(captor.capture());
        assertEquals(InboundStatus.NEW, captor.getValue().getStatus());
        assertEquals(1, captor.getValue().getItems().size());
    }

    @Test
    void createInboundOrder_ShouldThrowException_WhenWarehouseNotFound() {
        CreateInboundOrderDto inputDto = new CreateInboundOrderDto(1L, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of());

        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.createInboundOrder(inputDto));
        verify(warehouseRepository).findById(1L);
        verifyNoInteractions(supplierRepository, inboundOrderRepository, inboundOrderMapper);
    }

    @Test
    void createInboundOrder_ShouldThrowException_WhenSupplierNotFound() {
        CreateInboundOrderDto inputDto = new CreateInboundOrderDto(
                1L, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(),
                List.of(new InboundItemDto(null, null, 10L, 3, null)));

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.createInboundOrder(inputDto));
        verify(supplierRepository).findById(1L);
        verifyNoInteractions(inboundOrderRepository, inboundOrderMapper);
    }


    @Test
    void editInboundOrder_ShouldUpdateAndReturnDto_WhenValid() {
        Long orderId = 1L;
        InboundOrderDto updateDto = new InboundOrderDto(
                orderId, 2L, 2L, "Supplier B", InboundStatus.NEW, LocalDateTime.now(), List.of());
        InboundOrder existingOrder = new InboundOrder();
        existingOrder.setId(orderId);
        existingOrder.setStatus(InboundStatus.NEW);
        Warehouse warehouse = new Warehouse();
        Supplier supplier = new Supplier();

        when(inboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));
        when(inboundOrderMapper.toDto(existingOrder)).thenReturn(updateDto);

        InboundOrderDto result = inboundOrderService.editInboundOrder(orderId, updateDto);

        assertEquals(updateDto, result);
        assertSame(warehouse, existingOrder.getWarehouse());
        assertSame(supplier, existingOrder.getSupplier());
        verify(inboundOrderRepository, never()).save(any());
    }

    @Test
    void editInboundOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;
        InboundOrderDto updateDto = new InboundOrderDto(orderId, 1L, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of());

        when(inboundOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.editInboundOrder(orderId, updateDto));
        verify(inboundOrderRepository).findById(orderId);
        verifyNoInteractions(warehouseRepository, supplierRepository);
    }

    @Test
    void editInboundOrder_ShouldThrowException_WhenWarehouseNotFound() {
        Long orderId = 1L;
        InboundOrderDto updateDto = new InboundOrderDto(
                orderId, 1L, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of());
        InboundOrder existingOrder = new InboundOrder();
        existingOrder.setStatus(InboundStatus.NEW);

        when(inboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.editInboundOrder(orderId, updateDto));
        verify(warehouseRepository).findById(1L);
        verifyNoInteractions(supplierRepository);
    }

    @Test
    void editInboundOrder_ShouldThrowException_WhenSupplierNotFound() {
        Long orderId = 1L;
        InboundOrderDto updateDto = new InboundOrderDto(
                orderId, 1L, 1L, "Supplier A", InboundStatus.NEW, LocalDateTime.now(), List.of());
        InboundOrder existingOrder = new InboundOrder();
        existingOrder.setStatus(InboundStatus.NEW);

        when(inboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inboundOrderService.editInboundOrder(orderId, updateDto));
        verify(supplierRepository).findById(1L);
    }
}