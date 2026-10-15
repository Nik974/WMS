package com.backend.wms.service;

import com.backend.wms.dto.CreateInboundItemDto;
import com.backend.wms.dto.CreateOutboundItemDto;
import com.backend.wms.dto.InboundItemDto;
import com.backend.wms.entity.InboundItem;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.InboundItemMapper;
import com.backend.wms.repository.InboundItemRepository;
import com.backend.wms.repository.InboundOrderRepository;
import com.backend.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboundItemService {

    private final InboundOrderRepository inboundOrderRepository;
    private final InboundItemRepository inboundItemRepository;
    private final InboundItemMapper inboundItemMapper;
    private final ProductRepository productRepository;

    public List<InboundItemDto> getAllInboundItemsByOrderId(Long id){
        if (!inboundOrderRepository.existsById(id)){
            throw new ResourceNotFoundException("Inbound order not found with id: " + id);
        }
        return inboundItemMapper.toDtoList(inboundItemRepository.findAllByOrderId(id));
    }

    public List<InboundItemDto> getAllInboundItemsByProductId(Long id){
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return inboundItemMapper.toDtoList(inboundItemRepository.findAllByProductId(id));
    }

    @Transactional
    public InboundItemDto createInboundItem(CreateInboundItemDto dto){

        if (!productRepository.existsById(dto.productId())){
            throw new ResourceNotFoundException("Product not found with id: " + dto.productId());
        }
        if (!inboundOrderRepository.existsById(dto.orderId())){
            throw new ResourceNotFoundException("Inbound order not found with id: " + dto.orderId());
        }

        InboundItem inboundItem = inboundItemMapper.toEntity(dto);
        InboundItem savedItem = inboundItemRepository.save(inboundItem);

        return inboundItemMapper.toDto(savedItem);
    }

    @Transactional
    public InboundItemDto updateInboundItem(Long id, InboundItemDto dto){

        InboundItem existingInboundItem = inboundItemRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Inbound item not found with id: " + id));

        if (!productRepository.existsById(dto.productId())){
            throw new ResourceNotFoundException("Product not found with id: " + dto.productId());
        }
        if (!inboundOrderRepository.existsById(dto.orderId())){
            throw new ResourceNotFoundException("Inbound order not found with id: " + dto.orderId());
        }

        existingInboundItem.setProduct(productRepository.getReferenceById(dto.productId()));
        existingInboundItem.setOrder(inboundOrderRepository.getReferenceById(dto.orderId()));
        existingInboundItem.setQuantity(dto.quantity());

        InboundItem updatedItem = inboundItemRepository.save(existingInboundItem);
        return inboundItemMapper.toDto(updatedItem);
    }

}
