package com.backend.wms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.wms.dto.CreateOutboundOrderDto;
import com.backend.wms.dto.OutboundItemDto;
import com.backend.wms.dto.OutboundOrderDto;
import com.backend.wms.entity.*;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.OutboundOrderMapper;
import com.backend.wms.repository.CustomerRepository;
import com.backend.wms.repository.OutboundOrderRepository;
import com.backend.wms.repository.ProductRepository;
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
class OutboundOrderServiceTest {

    @InjectMocks
    private OutboundOrderService outboundOrderService;

    @Mock
    private OutboundOrderRepository outboundOrderRepository;

    @Mock
    private OutboundOrderMapper outboundOrderMapper;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void getOutboundOrderById_ShouldReturnDto_WhenOrderExists() {
        Long orderId = 1L;
        OutboundOrder order = new OutboundOrder();
        OutboundOrderDto expectedDto = new OutboundOrderDto(orderId, 1L, 1L, 1L, "NEW", OutboundStatus.NEW, LocalDateTime.now(), List.of());

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(outboundOrderMapper.toDto(order)).thenReturn(expectedDto);

        OutboundOrderDto result = outboundOrderService.getOutboundOrderById(orderId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(outboundOrderRepository).findById(orderId);
        verify(outboundOrderMapper).toDto(order);
    }

    @Test
    void getOutboundOrderById_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;
        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.getOutboundOrderById(orderId));
        verify(outboundOrderRepository).findById(orderId);
        verifyNoInteractions(outboundOrderMapper);
    }

    @Test
    void getAllOutboundOrders_ShouldReturnListOfDtos() {
        OutboundOrder order = new OutboundOrder();
        List<OutboundOrder> orders = List.of(order);
        List<OutboundOrderDto> expectedDtos = List.of(new OutboundOrderDto(1L, 1L, 1L, 1L, "NEW", OutboundStatus.NEW, LocalDateTime.now(), List.of()));

        when(outboundOrderRepository.findAll()).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderDto> result = outboundOrderService.getAllOutboundOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(outboundOrderRepository).findAll();
        verify(outboundOrderMapper).toDtoList(orders);
    }

    @Test
    void getOutboundOrdersByCustomerId_ShouldReturnListOfDtos_WhenCustomerExists() {
        Long customerId = 1L;
        OutboundOrder order = new OutboundOrder();
        List<OutboundOrder> orders = List.of(order);
        List<OutboundOrderDto> expectedDtos = List.of(new OutboundOrderDto(1L, customerId, 1L, 1L, "NEW", OutboundStatus.NEW, LocalDateTime.now(), List.of()));

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(outboundOrderRepository.findAllByCustomerId(customerId)).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderDto> result = outboundOrderService.getOutboundOrdersByCustomerId(customerId);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(customerRepository).existsById(customerId);
        verify(outboundOrderRepository).findAllByCustomerId(customerId);
    }

    @Test
    void getOutboundOrdersByCustomerId_ShouldThrowException_WhenCustomerNotFound() {
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.getOutboundOrdersByCustomerId(customerId));
        verify(customerRepository).existsById(customerId);
        verifyNoInteractions(outboundOrderRepository);
    }

    @Test
    void getOutboundOrdersByWarehouseId_ShouldReturnListOfDtos_WhenWarehouseExists() {
        Long warehouseId = 1L;
        OutboundOrder order = new OutboundOrder();
        List<OutboundOrder> orders = List.of(order);
        List<OutboundOrderDto> expectedDtos = List.of(new OutboundOrderDto(1L, 1L, warehouseId, 1L, "NEW", OutboundStatus.NEW, LocalDateTime.now(), List.of()));

        when(warehouseRepository.existsById(warehouseId)).thenReturn(true);
        when(outboundOrderRepository.findAllByWarehouseId(warehouseId)).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderDto> result = outboundOrderService.getOutboundOrdersByWarehouseId(warehouseId);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(warehouseRepository).existsById(warehouseId);
        verify(outboundOrderRepository).findAllByWarehouseId(warehouseId);
    }

    @Test
    void getOutboundOrdersByWarehouseId_ShouldThrowException_WhenWarehouseNotFound() {
        Long warehouseId = 1L;
        when(warehouseRepository.existsById(warehouseId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.getOutboundOrdersByWarehouseId(warehouseId));
        verify(warehouseRepository).existsById(warehouseId);
        verifyNoInteractions(outboundOrderRepository);
    }

    @Test
    void getOutboundOrdersByStatus_ShouldReturnListOfDtos() {
        OutboundStatus status = OutboundStatus.NEW;
        OutboundOrder order = new OutboundOrder();
        List<OutboundOrder> orders = List.of(order);
        List<OutboundOrderDto> expectedDtos = List.of(new OutboundOrderDto(1L, 1L, 1L, 1L, "NEW", OutboundStatus.NEW, LocalDateTime.now(), List.of()));

        when(outboundOrderRepository.findAllByStatus(status)).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderDto> result = outboundOrderService.getOutboundOrdersByStatus(status);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(outboundOrderRepository).findAllByStatus(status);
    }

    @Test
    void getOutboundOrdersByCreatedAtBetween_ShouldReturnListOfDtos() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        OutboundOrder order = new OutboundOrder();
        List<OutboundOrder> orders = List.of(order);
        List<OutboundOrderDto> expectedDtos = List.of(new OutboundOrderDto(1L, 1L, 1L, 1L, "NEW", OutboundStatus.NEW, LocalDateTime.now(), List.of()));

        when(outboundOrderRepository.findAllByCreatedAtBetween(start, end)).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderDto> result = outboundOrderService.getOutboundOrdersByCreatedAtBetween(start, end);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(outboundOrderRepository).findAllByCreatedAtBetween(start, end);
    }

    @Test
    void createOutboundOrder_ShouldSaveAndReturnDto_WhenValid() {
        CreateOutboundOrderDto dto = new CreateOutboundOrderDto(
                null, 1L, 2L, null, null, null,
                List.of(new OutboundItemDto(null, null, 10L, 3, null)));
        OutboundOrderDto expectedDto = new OutboundOrderDto( 1L,null, 1L, 2L, null, null, null, List.of(new OutboundItemDto(null, null, 10L, 3, null)));

        Customer customer = new Customer();
        Warehouse warehouse = new Warehouse();
        Product product = new Product();
        product.setId(10L);

        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findAllById(Set.of(10L))).thenReturn(List.of(product));
        when(outboundOrderRepository.save(any(OutboundOrder.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(outboundOrderMapper.toDto(any(OutboundOrder.class))).thenReturn(expectedDto);

        OutboundOrderDto result = outboundOrderService.createOutboundOrder(dto);

        assertEquals(expectedDto, result);

        ArgumentCaptor<OutboundOrder> captor = ArgumentCaptor.forClass(OutboundOrder.class);
        verify(outboundOrderRepository).save(captor.capture());
        assertEquals(OutboundStatus.NEW, captor.getValue().getStatus());
        assertEquals(1, captor.getValue().getItems().size());
    }

    @Test
    void createOutboundOrder_ShouldThrowException_WhenCustomerNotFound() {
        CreateOutboundOrderDto inputDto = new CreateOutboundOrderDto(1L, 1L, 1L, "New", OutboundStatus.NEW, LocalDateTime.now(), List.of());

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.createOutboundOrder(inputDto));
        verify(customerRepository).findById(1L);
        verifyNoInteractions(outboundOrderMapper, outboundOrderRepository);
    }

    @Test
    void editOutboundOrder_ShouldUpdateAndReturnDto_WhenValid() {
        Long orderId = 1L;
        OutboundOrderDto updateDto = new OutboundOrderDto(
                orderId, orderId, 2L, 2L, "Updated", OutboundStatus.NEW, LocalDateTime.now(), List.of());
        OutboundOrderDto expectedDto = new OutboundOrderDto(
                orderId, orderId, 2L, 2L, "Updated", OutboundStatus.NEW, LocalDateTime.now(), List.of());

        OutboundOrder existingOrder = new OutboundOrder();
        existingOrder.setId(orderId);
        existingOrder.setStatus(OutboundStatus.NEW);
        Customer customer = new Customer();
        Warehouse warehouse = new Warehouse();

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(outboundOrderMapper.toDto(existingOrder)).thenReturn(expectedDto);

        OutboundOrderDto result = outboundOrderService.editOutboundOrder(orderId, updateDto);

        assertEquals(expectedDto, result);
        assertSame(customer, existingOrder.getCustomer());
        assertSame(warehouse, existingOrder.getWarehouse());
        verify(outboundOrderRepository, never()).save(any());
    }

    @Test
    void editOutboundOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;
        OutboundOrderDto updateDto = new OutboundOrderDto(orderId, 1L, 1L, 1L, "Updated", OutboundStatus.NEW, LocalDateTime.now(), List.of());

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.editOutboundOrder(orderId, updateDto));
        verify(outboundOrderRepository).findById(orderId);
        verifyNoInteractions(customerRepository, warehouseRepository);
    }

    @Test
    void editOutboundOrder_ShouldThrowException_WhenCustomerNotFound() {
        Long orderId = 1L;
        OutboundOrderDto updateDto = new OutboundOrderDto(
                orderId, 1L, 1L, 1L, "Updated", OutboundStatus.NEW, LocalDateTime.now(), List.of());
        OutboundOrder existingOrder = new OutboundOrder();
        existingOrder.setStatus(OutboundStatus.NEW);

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> outboundOrderService.editOutboundOrder(orderId, updateDto));
        verify(customerRepository).findById(1L);
        verifyNoInteractions(warehouseRepository);
    }


    @Test
    void editOutboundOrder_ShouldThrowException_WhenWarehouseNotFound() {
        Long orderId = 1L;
        OutboundOrderDto updateDto = new OutboundOrderDto(
                orderId, 1L, 1L, 1L, "Updated", OutboundStatus.NEW, LocalDateTime.now(), List.of());
        OutboundOrder existingOrder = new OutboundOrder();
        existingOrder.setStatus(OutboundStatus.NEW);

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> outboundOrderService.editOutboundOrder(orderId, updateDto));
        verify(warehouseRepository).findById(1L);
    }
    @Test
    void changeStatus_ShouldUpdateStatus_WhenTransitionAllowed() {
        OutboundOrder order = new OutboundOrder();
        order.setStatus(OutboundStatus.NEW);
        OutboundOrderDto dto = new OutboundOrderDto(
                1L, 1L, 1L, 1L, "c", OutboundStatus.IN_TRANSIT, LocalDateTime.now(), List.of());

        when(outboundOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(outboundOrderMapper.toDto(order)).thenReturn(dto);

        OutboundOrderDto result = outboundOrderService.changeStatus(1L, OutboundStatus.IN_TRANSIT);

        assertEquals(dto, result);
        assertEquals(OutboundStatus.IN_TRANSIT, order.getStatus());
    }

    @Test
    void changeStatus_ShouldThrow_WhenTransitionNotAllowed() {
        OutboundOrder order = new OutboundOrder();
        order.setStatus(OutboundStatus.SHIPPED);

        when(outboundOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
                () -> outboundOrderService.changeStatus(1L, OutboundStatus.NEW));
        assertEquals(OutboundStatus.SHIPPED, order.getStatus());
        verifyNoInteractions(outboundOrderMapper);
    }

    @Test
    void changeStatus_ShouldThrow_WhenOrderNotFound() {
        when(outboundOrderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> outboundOrderService.changeStatus(1L, OutboundStatus.IN_TRANSIT));
    }
}