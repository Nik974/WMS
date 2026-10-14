package com.backend.wms.service;

import com.backend.wms.dto.CreateOutboundOrderDto;
import com.backend.wms.dto.OutboundOrderRequestDto;
import com.backend.wms.entity.OutboundOrder;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.OutboundOrderMapper;
import com.backend.wms.repository.CustomerRepository;
import com.backend.wms.repository.OutboundOrderRepository;
import com.backend.wms.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboundOrderService {

    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundOrderMapper outboundOrderMapper;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;

    public OutboundOrderRequestDto getOutboundOrderById(Long orderId) {
        return outboundOrderRepository.findById(orderId)
                .map(outboundOrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound order not found with id: " + orderId));
    }

    public List<OutboundOrderRequestDto> getAllOutboundOrders() {
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAll());
    }

    public List<OutboundOrderRequestDto> getOutboundOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAllByCustomerId(customerId));
    }

    public List<OutboundOrderRequestDto> getOutboundOrdersByWarehouseId(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + warehouseId);
        }
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAllByWarehouseId(warehouseId));
    }

    public List<OutboundOrderRequestDto> getOutboundOrdersByStatus(String status) {
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAllByStatus(status));
    }
    public List<OutboundOrderRequestDto> getOutboundOrdersByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAllByCreatedAtBetween(start, end));
    }

    @Transactional
    public OutboundOrderRequestDto createOutboundOrder(CreateOutboundOrderDto createOutboundOrderDto) {

        if (!customerRepository.existsById(createOutboundOrderDto.customerId())) {
            throw new ResourceNotFoundException("Customer not found with id: " + createOutboundOrderDto.customerId());
        }

        if (!warehouseRepository.existsById(createOutboundOrderDto.warehouseId())) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + createOutboundOrderDto.warehouseId());
        }

        OutboundOrder outboundOrder = outboundOrderMapper.toEntity(createOutboundOrderDto);
        OutboundOrder savedOutboundOrder = outboundOrderRepository.save(outboundOrder);
        return outboundOrderMapper.toDto(savedOutboundOrder);
    }

    @Transactional
    public OutboundOrderRequestDto editOutboundOrder(Long orderId, OutboundOrderRequestDto updateDto) {
        OutboundOrder existingOrder = outboundOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound order not found with id: " + orderId));

        if (!customerRepository.existsById(updateDto.customerId())) {
            throw new ResourceNotFoundException("Customer not found with id: " + updateDto.customerId());
        }
        if (!warehouseRepository.existsById(updateDto.warehouseId())) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + updateDto.warehouseId());
        }

        existingOrder.setCustomer(customerRepository.getReferenceById(updateDto.customerId()));
        existingOrder.setStatus(updateDto.status());
        existingOrder.setWarehouse(warehouseRepository.getReferenceById(updateDto.warehouseId()));

        OutboundOrder updatedOrder = outboundOrderRepository.save(existingOrder);
        return outboundOrderMapper.toDto(updatedOrder);
    }
}