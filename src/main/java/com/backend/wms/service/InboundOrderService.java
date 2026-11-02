package com.backend.wms.service;

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
public class InboundOrderService {

    private final InboundOrderRepository inboundOrderRepository;
    private final InboundOrderMapper inboundOrderMapper;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    private static final Map<InboundStatus, Set<InboundStatus>> ALLOWED = Map.of(
            InboundStatus.NEW, Set.of(InboundStatus.RECEIVED, InboundStatus.CANCELLED),
            InboundStatus.RECEIVED, Set.of(),
            InboundStatus.CANCELLED, Set.of()
    );

    public InboundOrderDto getInboundOrderById(Long id) {
        return inboundOrderRepository.findById(id)
                .map(inboundOrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));
    }

    public List<InboundOrderDto> getAllInboundOrders() {
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAll());
    }

    public List<InboundOrderDto> getInboundOrdersBySupplierId(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllBySupplierId(supplierId));
    }

    public List<InboundOrderDto> getInboundOrdersByWarehouseId(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + warehouseId);
        }
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllByWarehouseId(warehouseId));
    }

    public List<InboundOrderDto> getInboundOrdersByStatus(InboundStatus status) {
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllByStatus(status));
    }

    public List<InboundOrderDto> getInboundOrdersByCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllByCreatedAtBetween(start, end));
    }

    @Transactional
    public InboundOrderDto createInboundOrder(CreateInboundOrderDto dto) {
        Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + dto.warehouseId()));
        Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + dto.supplierId()));

        Set<Long> productIds = dto.items().stream()
                .map(InboundItemDto::productId)
                .collect(Collectors.toSet());
        Map<Long, Product> products = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        if (products.size() != productIds.size()) {
            throw new ResourceNotFoundException("Some products not found");
        }

        InboundOrder order = new InboundOrder();
        order.setWarehouse(warehouse);
        order.setSupplier(supplier);
        order.setStatus(InboundStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        for (InboundItemDto i : dto.items()) {
            InboundItem item = new InboundItem();
            item.setOrder(order);
            item.setProduct(products.get(i.productId()));
            item.setQuantity(i.quantity());
            order.getItems().add(item);
        }

        return inboundOrderMapper.toDto(inboundOrderRepository.save(order));
    }

    @Transactional
    public InboundOrderDto editInboundOrder(Long id, InboundOrderDto dto) {
        InboundOrder order = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));

        if (order.getStatus() != InboundStatus.NEW) {
            throw new IllegalStateException("Only orders in NEW status can be edited");
        }

        order.setWarehouse(warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + dto.warehouseId())));
        order.setSupplier(supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + dto.supplierId())));

        return inboundOrderMapper.toDto(order);
    }

    @Transactional
    public InboundOrderDto changeStatus(Long id, InboundStatus newStatus) {
        InboundOrder order = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));

        if (!ALLOWED.get(order.getStatus()).contains(newStatus)) {
            throw new IllegalStateException("Cannot change status from " + order.getStatus() + " to " + newStatus);
        }
        order.setStatus(newStatus);
        return inboundOrderMapper.toDto(order);
    }
}
