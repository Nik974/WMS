package com.backend.wms.service;

import com.backend.wms.dto.CreateInboundOrderDto;
import com.backend.wms.dto.InboundOrderDto;
import com.backend.wms.entity.InboundOrder;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.InboundOrderMapper;
import com.backend.wms.repository.InboundOrderRepository;
import com.backend.wms.repository.SupplierRepository;
import com.backend.wms.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboundOrderService {

    private final InboundOrderRepository inboundOrderRepository;
    private final InboundOrderMapper inboundOrderMapper;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;

    public List<InboundOrderDto> getAllInboundOrders(){
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAll());
    }

    public List<InboundOrderDto> getInboundOrdersBySupplierId(Long supplierId){
        if (!supplierRepository.existsById(supplierId)){
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllBySupplierId(supplierId));
    }

    public List<InboundOrderDto> getInboundOrdersByWarehouseId(Long warehouseId){
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + warehouseId);
        }
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllByWarehouseId(warehouseId));
    }

    public List<InboundOrderDto> getInboundOrdersByStatus(String status){
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findAllByStatus(status));
    }

    public List<InboundOrderDto> getInboundOrdersByCreatedBetween(LocalDateTime start, LocalDateTime end){
        return inboundOrderMapper.toDtoList(inboundOrderRepository.findTopByCreatedAtBetween(start, end));
    }

    @Transactional
    public InboundOrderDto createInboundOrder(CreateInboundOrderDto createInboundOrderDto){
        if (!warehouseRepository.existsById(createInboundOrderDto.warehouseId())){
            throw new ResourceNotFoundException("Warehouse not found with id: " + createInboundOrderDto.warehouseId());
        }
        if (!supplierRepository.existsById(createInboundOrderDto.supplierId())){
            throw new ResourceNotFoundException("Supplier not found with id: " + createInboundOrderDto.supplierId());
        }
        InboundOrder inboundOrder = inboundOrderMapper.toEntity(createInboundOrderDto);
        InboundOrder savedInboundOrder = inboundOrderRepository.save(inboundOrder);
        return inboundOrderMapper.toDto(savedInboundOrder);
    }

    @Transactional
    public InboundOrderDto editInboundOrder(Long id, InboundOrderDto dto){
        InboundOrder existingOrder = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));
        if (!warehouseRepository.existsById(dto.warehouseId())){
            throw new ResourceNotFoundException("Warehouse not found with id: " + dto.warehouseId());
        }
        if (!supplierRepository.existsById(dto.supplierId())){
            throw new ResourceNotFoundException("Supplier not found with id: " + dto.supplierId());
        }
        existingOrder.setSupplier(supplierRepository.getReferenceById(dto.supplierId()));
        existingOrder.setWarehouse(warehouseRepository.getReferenceById(dto.warehouseId()));
        existingOrder.setStatus(dto.status());

        InboundOrder updatedOrder = inboundOrderRepository.save(existingOrder);
        return inboundOrderMapper.toDto(updatedOrder);
    }
}
