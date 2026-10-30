package com.backend.wms.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboundOrderService {

    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundOrderMapper outboundOrderMapper;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private static final Map<OutboundStatus, Set<OutboundStatus>> ALLOWED = Map.of(
            OutboundStatus.NEW, Set.of(OutboundStatus.IN_TRANSIT, OutboundStatus.CANCELLED),
            OutboundStatus.IN_TRANSIT, Set.of(OutboundStatus.SHIPPED, OutboundStatus.CANCELLED),
            OutboundStatus.SHIPPED, Set.of(),
            OutboundStatus.CANCELLED, Set.of()
    );

    public OutboundOrderDto getOutboundOrderById(Long orderId) {
        return outboundOrderRepository.findById(orderId)
                .map(outboundOrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound order not found with id: " + orderId));
    }

    public List<OutboundOrderDto> getAllOutboundOrders() {
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAll());
    }

    public List<OutboundOrderDto> getOutboundOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAllByCustomerId(customerId));
    }

    public List<OutboundOrderDto> getOutboundOrdersByWarehouseId(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + warehouseId);
        }
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAllByWarehouseId(warehouseId));
    }

    public List<OutboundOrderDto> getOutboundOrdersByStatus(OutboundStatus status) {
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAllByStatus(status));
    }

    public List<OutboundOrderDto> getOutboundOrdersByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return outboundOrderMapper.toDtoList(outboundOrderRepository.findAllByCreatedAtBetween(start, end));
    }

    @Transactional
    public OutboundOrderDto createOutboundOrder(CreateOutboundOrderDto dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.customerId()));
        Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + dto.warehouseId()));

        Set<Long> productIds = dto.items().stream()
                .map(OutboundItemDto::productId)
                .collect(Collectors.toSet());
        Map<Long, Product> products = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        if (products.size() != productIds.size()) {
            throw new ResourceNotFoundException("Some products not found");
        }

        OutboundOrder order = new OutboundOrder();
        order.setCustomer(customer);
        order.setWarehouse(warehouse);
        order.setStatus(OutboundStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        for (OutboundItemDto i : dto.items()) {
            OutboundItem item = new OutboundItem();
            item.setOrder(order);
            item.setProduct(products.get(i.productId()));
            item.setQuantity(i.quantity());
            order.getItems().add(item);
        }

        return outboundOrderMapper.toDto(outboundOrderRepository.save(order));
    }

    @Transactional
    public OutboundOrderDto editOutboundOrder(Long orderId, OutboundOrderDto dto) {
        OutboundOrder order = outboundOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound order not found with id: " + orderId));

        if (order.getStatus() != OutboundStatus.NEW) {
            throw new IllegalStateException("Only orders in NEW status can be edited");
        }

        order.setCustomer(customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.customerId())));
        order.setWarehouse(warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + dto.warehouseId())));

        return outboundOrderMapper.toDto(order);
    }

    @Transactional
    public OutboundOrderDto changeStatus(Long orderId, OutboundStatus newStatus) {
        OutboundOrder order = outboundOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound order not found with id: " + orderId));

        if (!ALLOWED.get(order.getStatus()).contains(newStatus)) {
            throw new IllegalStateException(
                    "Cannot change status from " + order.getStatus() + " to " + newStatus);
        }
        order.setStatus(newStatus);
        return outboundOrderMapper.toDto(order);
    }
}