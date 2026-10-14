package com.backend.wms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.wms.dto.CreateOutboundOrderDto;
import com.backend.wms.dto.OutboundOrderRequestDto;
import com.backend.wms.entity.Customer;
import com.backend.wms.entity.OutboundOrder;
import com.backend.wms.entity.Warehouse;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.OutboundOrderMapper;
import com.backend.wms.repository.CustomerRepository;
import com.backend.wms.repository.OutboundOrderRepository;
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

    @Test
    void getOutboundOrderById_ShouldReturnDto_WhenOrderExists() {
        Long orderId = 1L;
        OutboundOrder order = new OutboundOrder();
        OutboundOrderRequestDto expectedDto = new OutboundOrderRequestDto(orderId, 1L, 1L, 1L, "NEW", "Ready", LocalDateTime.now(), List.of());

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(outboundOrderMapper.toDto(order)).thenReturn(expectedDto);

        OutboundOrderRequestDto result = outboundOrderService.getOutboundOrderById(orderId);

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
        List<OutboundOrderRequestDto> expectedDtos = List.of(new OutboundOrderRequestDto(1L, 1L, 1L, 1L, "NEW", "Ready", LocalDateTime.now(), List.of()));

        when(outboundOrderRepository.findAll()).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderRequestDto> result = outboundOrderService.getAllOutboundOrders();

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
        List<OutboundOrderRequestDto> expectedDtos = List.of(new OutboundOrderRequestDto(1L, customerId, 1L, 1L, "NEW", "Ready", LocalDateTime.now(), List.of()));

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(outboundOrderRepository.findAllByCustomerId(customerId)).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderRequestDto> result = outboundOrderService.getOutboundOrdersByCustomerId(customerId);

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
        List<OutboundOrderRequestDto> expectedDtos = List.of(new OutboundOrderRequestDto(1L, 1L, warehouseId, 1L, "NEW", "Ready", LocalDateTime.now(), List.of()));

        when(warehouseRepository.existsById(warehouseId)).thenReturn(true);
        when(outboundOrderRepository.findAllByWarehouseId(warehouseId)).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderRequestDto> result = outboundOrderService.getOutboundOrdersByWarehouseId(warehouseId);

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
        String status = "NEW";
        OutboundOrder order = new OutboundOrder();
        List<OutboundOrder> orders = List.of(order);
        List<OutboundOrderRequestDto> expectedDtos = List.of(new OutboundOrderRequestDto(1L, 1L, 1L, 1L, status, "Ready", LocalDateTime.now(), List.of()));

        when(outboundOrderRepository.findAllByStatus(status)).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderRequestDto> result = outboundOrderService.getOutboundOrdersByStatus(status);

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
        List<OutboundOrderRequestDto> expectedDtos = List.of(new OutboundOrderRequestDto(1L, 1L, 1L, 1L, "NEW", "Ready", LocalDateTime.now(), List.of()));

        when(outboundOrderRepository.findAllByCreatedAtBetween(start, end)).thenReturn(orders);
        when(outboundOrderMapper.toDtoList(orders)).thenReturn(expectedDtos);

        List<OutboundOrderRequestDto> result = outboundOrderService.getOutboundOrdersByCreatedAtBetween(start, end);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(outboundOrderRepository).findAllByCreatedAtBetween(start, end);
    }

    @Test
    void createOutboundOrder_ShouldSaveAndReturnDto_WhenValid() {
        CreateOutboundOrderDto inputDto = new CreateOutboundOrderDto(1L, 1L, 1L, "New", "NEW", LocalDateTime.now(), List.of());
        OutboundOrder entity = new OutboundOrder();
        OutboundOrder savedEntity = new OutboundOrder();
        savedEntity.setId(1L);
        OutboundOrderRequestDto expectedDto = new OutboundOrderRequestDto(1L, 1L, 1L, 1L, "NEW", "Ready", LocalDateTime.now(), List.of());

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(outboundOrderMapper.toEntity(inputDto)).thenReturn(entity);
        when(outboundOrderRepository.save(entity)).thenReturn(savedEntity);
        when(outboundOrderMapper.toDto(savedEntity)).thenReturn(expectedDto);
        when(warehouseRepository.existsById(1L)).thenReturn(true);

        OutboundOrderRequestDto result = outboundOrderService.createOutboundOrder(inputDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(customerRepository).existsById(1L);
        verify(warehouseRepository).existsById(1L);
        verify(outboundOrderRepository).save(entity);
    }

    @Test
    void createOutboundOrder_ShouldThrowException_WhenCustomerNotFound() {
        CreateOutboundOrderDto inputDto = new CreateOutboundOrderDto(1L, 1L, 1L, "New", "NEW", LocalDateTime.now(), List.of());

        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.createOutboundOrder(inputDto));
        verify(customerRepository).existsById(1L);
        verifyNoInteractions(outboundOrderMapper, outboundOrderRepository);
    }

    @Test
    void editOutboundOrder_ShouldUpdateAndReturnDto_WhenValid() {
        Long orderId = 1L;
        OutboundOrderRequestDto updateDto = new OutboundOrderRequestDto(orderId, 2L, 2L, 2L, "Updated", "PROCESSING", LocalDateTime.now(), List.of());
        OutboundOrder existingOrder = new OutboundOrder();
        existingOrder.setId(orderId);
        OutboundOrderRequestDto expectedDto = new OutboundOrderRequestDto(orderId, 2L, 2L, 2L, "PROCESSING", "Updated", LocalDateTime.now(), List.of());

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(customerRepository.existsById(2L)).thenReturn(true);
        when(warehouseRepository.existsById(2L)).thenReturn(true);
        when(customerRepository.getReferenceById(2L)).thenReturn(new Customer());
        when(warehouseRepository.getReferenceById(2L)).thenReturn(new Warehouse());
        when(outboundOrderRepository.save(existingOrder)).thenReturn(existingOrder);
        when(outboundOrderMapper.toDto(existingOrder)).thenReturn(expectedDto);

        OutboundOrderRequestDto result = outboundOrderService.editOutboundOrder(orderId, updateDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(outboundOrderRepository).findById(orderId);
        verify(customerRepository).existsById(2L);
        verify(warehouseRepository).existsById(2L);
        verify(outboundOrderRepository).save(existingOrder);
    }

    @Test
    void editOutboundOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;
        OutboundOrderRequestDto updateDto = new OutboundOrderRequestDto(orderId, 1L, 1L, 1L, "Updated", "NEW", LocalDateTime.now(), List.of());

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.editOutboundOrder(orderId, updateDto));
        verify(outboundOrderRepository).findById(orderId);
        verifyNoInteractions(customerRepository, warehouseRepository);
    }

    @Test
    void editOutboundOrder_ShouldThrowException_WhenCustomerNotFound() {
        Long orderId = 1L;
        OutboundOrderRequestDto updateDto = new OutboundOrderRequestDto(orderId, 1L, 1L, 1L, "Updated", "NEW", LocalDateTime.now(), List.of());
        OutboundOrder existingOrder = new OutboundOrder();

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.editOutboundOrder(orderId, updateDto));
        verify(outboundOrderRepository).findById(orderId);
        verify(customerRepository).existsById(1L);
        verifyNoInteractions(warehouseRepository);
    }

    @Test
    void editOutboundOrder_ShouldThrowException_WhenWarehouseNotFound() {
        Long orderId = 1L;
        OutboundOrderRequestDto updateDto = new OutboundOrderRequestDto(orderId, 1L, 1L, 1L, "Updated", "NEW", LocalDateTime.now(), List.of());
        OutboundOrder existingOrder = new OutboundOrder();

        when(outboundOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(warehouseRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundOrderService.editOutboundOrder(orderId, updateDto));
        verify(outboundOrderRepository).findById(orderId);
        verify(customerRepository).existsById(1L);
        verify(warehouseRepository).existsById(1L);
    }
}