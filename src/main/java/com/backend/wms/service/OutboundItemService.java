package com.backend.wms.service;

import com.backend.wms.dto.CreateOutboundItemDto;
import com.backend.wms.dto.OutboundItemDto;
import com.backend.wms.entity.OutboundItem;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.OutboundItemMapper;
import com.backend.wms.repository.OutboundItemRepository;
import com.backend.wms.repository.OutboundOrderRepository;
import com.backend.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboundItemService {

    private final OutboundItemRepository outboundItemRepository;
    private final ProductRepository productRepository;
    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundItemMapper outboundItemMapper;

    public List<OutboundItemDto> getAllOutboundItemsByOrderId(Long orderId) {
        if (!outboundOrderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Outbound order not found with id: " + orderId);
        }
        return outboundItemMapper.toDtoList(outboundItemRepository.findAllByOrderId(orderId));
    }

    public List<OutboundItemDto> getAllOutboundItemsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return outboundItemMapper.toDtoList(outboundItemRepository.findAllByProductId(productId));
    }

    @Transactional
    public OutboundItemDto createOutboundItem(CreateOutboundItemDto createOutboundItemDto) {
        if (!productRepository.existsById(createOutboundItemDto.productId())) {
            throw new ResourceNotFoundException("Product not found with id: " + createOutboundItemDto.productId());
        }
        if (!outboundOrderRepository.existsById(createOutboundItemDto.orderId())) {
            throw new ResourceNotFoundException("Outbound order not found with id: " + createOutboundItemDto.orderId());
        }
        OutboundItem outboundItem = outboundItemMapper.toEntity(createOutboundItemDto);
        outboundItem.setOrder(outboundOrderRepository.findById(createOutboundItemDto.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found")));
        outboundItem.setProduct(productRepository.findById(createOutboundItemDto.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
        OutboundItem savedOutboundItem = outboundItemRepository.save(outboundItem);

        return outboundItemMapper.toDto(savedOutboundItem);
    }

    @Transactional
    public OutboundItemDto updateOutboundItem(Long itemId, OutboundItemDto dto) {
        OutboundItem existingOutboundItem = outboundItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound item not found with id: " + itemId));

        if (!productRepository.existsById(dto.productId())) {
            throw new ResourceNotFoundException("Product not found with id: " + dto.productId());
        }
        if (!outboundOrderRepository.existsById(dto.orderId())) {
            throw new ResourceNotFoundException("Outbound order not found with id: " + dto.orderId());
        }

        existingOutboundItem.setProduct(productRepository.getReferenceById(dto.productId()));
        existingOutboundItem.setOrder(outboundOrderRepository.getReferenceById(dto.orderId()));
        existingOutboundItem.setQuantity(dto.quantity());

        OutboundItem updatedOutboundItem = outboundItemRepository.save(existingOutboundItem);
        return outboundItemMapper.toDto(updatedOutboundItem);
    }
}
