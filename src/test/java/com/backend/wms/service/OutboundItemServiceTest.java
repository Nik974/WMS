package com.backend.wms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.wms.dto.CreateOutboundItemDto;
import com.backend.wms.dto.OutboundItemDto;
import com.backend.wms.entity.OutboundItem;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.OutboundItemMapper;
import com.backend.wms.repository.OutboundItemRepository;
import com.backend.wms.repository.OutboundOrderRepository;
import com.backend.wms.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboundItemServiceTest {

    @InjectMocks
    private OutboundItemService outboundItemService;

    @Mock
    private OutboundItemRepository outboundItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OutboundOrderRepository outboundOrderRepository;

    @Mock
    private OutboundItemMapper outboundItemMapper;

    @Test
    void getAllOutboundItemsByOrderId_ShouldReturnListOfDtos_WhenOrderExists() {
        Long orderId = 1L;
        OutboundItem item = new OutboundItem();
        List<OutboundItem> items = List.of(item);
        List<OutboundItemDto> expectedDtos = List.of(new OutboundItemDto(1L, orderId, 1L, 5));

        when(outboundOrderRepository.existsById(orderId)).thenReturn(true);
        when(outboundItemRepository.findAllByOrderId(orderId)).thenReturn(items);
        when(outboundItemMapper.toDtoList(items)).thenReturn(expectedDtos);

        List<OutboundItemDto> result = outboundItemService.getAllOutboundItemsByOrderId(orderId);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(outboundOrderRepository).existsById(orderId);
        verify(outboundItemRepository).findAllByOrderId(orderId);
        verify(outboundItemMapper).toDtoList(items);
    }

    @Test
    void getAllOutboundItemsByOrderId_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;
        when(outboundOrderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundItemService.getAllOutboundItemsByOrderId(orderId));
        verify(outboundOrderRepository).existsById(orderId);
        verifyNoInteractions(outboundItemRepository, outboundItemMapper);
    }

    @Test
    void getAllOutboundItemsByProductId_ShouldReturnListOfDtos_WhenProductExists() {
        Long productId = 1L;
        OutboundItem item = new OutboundItem();
        List<OutboundItem> items = List.of(item);
        List<OutboundItemDto> expectedDtos = List.of(new OutboundItemDto(1L, 1L, productId, 3));

        when(productRepository.existsById(productId)).thenReturn(true);
        when(outboundItemRepository.findAllByProductId(productId)).thenReturn(items);
        when(outboundItemMapper.toDtoList(items)).thenReturn(expectedDtos);

        List<OutboundItemDto> result = outboundItemService.getAllOutboundItemsByProductId(productId);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(productRepository).existsById(productId);
        verify(outboundItemRepository).findAllByProductId(productId);
        verify(outboundItemMapper).toDtoList(items);
    }

    @Test
    void getAllOutboundItemsByProductId_ShouldThrowException_WhenProductNotFound() {
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundItemService.getAllOutboundItemsByProductId(productId));
        verify(productRepository).existsById(productId);
        verifyNoInteractions(outboundItemRepository, outboundItemMapper);
    }

    @Test
    void createOutboundItem_ShouldSaveAndReturnDto_WhenValid() {
        CreateOutboundItemDto inputDto = new CreateOutboundItemDto(1L, 1L, 2);
        OutboundItem entity = new OutboundItem();
        OutboundItem savedEntity = new OutboundItem();
        savedEntity.setId(1L);
        OutboundItemDto expectedDto = new OutboundItemDto(1L, 1L, 1L, 2);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(outboundOrderRepository.existsById(1L)).thenReturn(true);
        when(outboundItemMapper.toEntity(inputDto)).thenReturn(entity);
        when(outboundItemRepository.save(entity)).thenReturn(savedEntity);
        when(outboundItemMapper.toDto(savedEntity)).thenReturn(expectedDto);

        OutboundItemDto result = outboundItemService.createOutboundItem(inputDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(productRepository).existsById(1L);
        verify(outboundOrderRepository).existsById(1L);
        verify(outboundItemRepository).save(entity);
    }

    @Test
    void createOutboundItem_ShouldThrowException_WhenProductNotFound() {
        CreateOutboundItemDto inputDto = new CreateOutboundItemDto(1L, 1L, 2);

        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundItemService.createOutboundItem(inputDto));
        verify(productRepository).existsById(1L);
        verifyNoInteractions(outboundOrderRepository, outboundItemRepository, outboundItemMapper);
    }

    @Test
    void createOutboundItem_ShouldThrowException_WhenOrderNotFound() {
        CreateOutboundItemDto inputDto = new CreateOutboundItemDto(1L, 1L, 2);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(outboundOrderRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundItemService.createOutboundItem(inputDto));
        verify(productRepository).existsById(1L);
        verify(outboundOrderRepository).existsById(1L);
        verifyNoInteractions(outboundItemRepository, outboundItemMapper);
    }

    @Test
    void updateOutboundItem_ShouldUpdateAndReturnDto_WhenValid() {
        Long itemId = 1L;
        OutboundItemDto updateDto = new OutboundItemDto(itemId, 2L, 2L, 4);
        OutboundItem existingItem = new OutboundItem();
        existingItem.setId(itemId);

        when(outboundItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(productRepository.existsById(2L)).thenReturn(true);
        when(outboundOrderRepository.existsById(2L)).thenReturn(true);
        when(outboundItemRepository.save(existingItem)).thenReturn(existingItem);
        when(outboundItemMapper.toDto(existingItem)).thenReturn(updateDto);

        OutboundItemDto result = outboundItemService.updateOutboundItem(itemId, updateDto);

        assertNotNull(result);
        assertEquals(updateDto, result);
        verify(outboundItemRepository).findById(itemId);
        verify(productRepository).existsById(2L);
        verify(outboundOrderRepository).existsById(2L);
        verify(outboundItemRepository).save(existingItem);
    }

    @Test
    void updateOutboundItem_ShouldThrowException_WhenItemNotFound() {
        Long itemId = 1L;
        OutboundItemDto updateDto = new OutboundItemDto(itemId, 1L, 1L, 2);

        when(outboundItemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> outboundItemService.updateOutboundItem(itemId, updateDto));
        verify(outboundItemRepository).findById(itemId);
        verifyNoInteractions(productRepository, outboundOrderRepository);
    }

    @Test
    void updateOutboundItem_ShouldThrowException_WhenProductNotFound() {
        Long itemId = 1L;
        OutboundItemDto updateDto = new OutboundItemDto(itemId, 1L, 1L, 2);
        OutboundItem existingItem = new OutboundItem();

        when(outboundItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundItemService.updateOutboundItem(itemId, updateDto));
        verify(outboundItemRepository).findById(itemId);
        verify(productRepository).existsById(1L);
        verifyNoInteractions(outboundOrderRepository);
    }

    @Test
    void updateOutboundItem_ShouldThrowException_WhenOrderNotFound() {
        Long itemId = 1L;
        OutboundItemDto updateDto = new OutboundItemDto(itemId, 1L, 1L, 2);
        OutboundItem existingItem = new OutboundItem();

        when(outboundItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(productRepository.existsById(1L)).thenReturn(true);
        when(outboundOrderRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> outboundItemService.updateOutboundItem(itemId, updateDto));
        verify(outboundItemRepository).findById(itemId);
        verify(productRepository).existsById(1L);
        verify(outboundOrderRepository).existsById(1L);
    }
}