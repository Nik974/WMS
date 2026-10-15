package com.backend.wms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.wms.dto.CreateInboundItemDto;
import com.backend.wms.dto.InboundItemDto;
import com.backend.wms.entity.InboundItem;
import com.backend.wms.entity.InboundOrder;
import com.backend.wms.entity.Product;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.InboundItemMapper;
import com.backend.wms.repository.InboundItemRepository;
import com.backend.wms.repository.InboundOrderRepository;
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
class InboundItemServiceTest {

    @InjectMocks
    private InboundItemService inboundItemService;

    @Mock
    private InboundItemRepository inboundItemRepository;

    @Mock
    private InboundOrderRepository inboundOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InboundItemMapper inboundItemMapper;

    @Test
    void getAllInboundItemsByOrderId_ShouldReturnListOfDtos_WhenOrderExists() {
        Long orderId = 1L;
        InboundItem item = new InboundItem();
        List<InboundItem> items = List.of(item);
        List<InboundItemDto> expectedDtos = List.of(new InboundItemDto(1L, orderId, 1L, 5));

        when(inboundOrderRepository.existsById(orderId)).thenReturn(true);
        when(inboundItemRepository.findAllByOrderId(orderId)).thenReturn(items);
        when(inboundItemMapper.toDtoList(items)).thenReturn(expectedDtos);

        List<InboundItemDto> result = inboundItemService.getAllInboundItemsByOrderId(orderId);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(inboundOrderRepository).existsById(orderId);
        verify(inboundItemRepository).findAllByOrderId(orderId);
    }

    @Test
    void getAllInboundItemsByOrderId_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;
        when(inboundOrderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundItemService.getAllInboundItemsByOrderId(orderId));
        verify(inboundOrderRepository).existsById(orderId);
        verifyNoInteractions(inboundItemRepository);
    }

    @Test
    void getAllInboundItemsByProductId_ShouldReturnListOfDtos_WhenProductExists() {
        Long productId = 1L;
        InboundItem item = new InboundItem();
        List<InboundItem> items = List.of(item);
        List<InboundItemDto> expectedDtos = List.of(new InboundItemDto(1L, 1L, productId, 5));

        when(productRepository.existsById(productId)).thenReturn(true);
        when(inboundItemRepository.findAllByProductId(productId)).thenReturn(items);
        when(inboundItemMapper.toDtoList(items)).thenReturn(expectedDtos);

        List<InboundItemDto> result = inboundItemService.getAllInboundItemsByProductId(productId);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(productRepository).existsById(productId);
        verify(inboundItemRepository).findAllByProductId(productId);
    }

    @Test
    void getAllInboundItemsByProductId_ShouldThrowException_WhenProductNotFound() {
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundItemService.getAllInboundItemsByProductId(productId));
        verify(productRepository).existsById(productId);
        verifyNoInteractions(inboundItemRepository);
    }

    @Test
    void createInboundItem_ShouldSaveAndReturnDto_WhenValid() {
        CreateInboundItemDto inputDto = new CreateInboundItemDto(1L, 1L, 10);
        InboundItem entity = new InboundItem();
        InboundItem savedEntity = new InboundItem();
        InboundItemDto expectedDto = new InboundItemDto(1L, 1L, 1L, 10);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(inboundOrderRepository.existsById(1L)).thenReturn(true);
        when(inboundItemMapper.toEntity(inputDto)).thenReturn(entity);
        when(inboundItemRepository.save(entity)).thenReturn(savedEntity);
        when(inboundItemMapper.toDto(savedEntity)).thenReturn(expectedDto);

        InboundItemDto result = inboundItemService.createInboundItem(inputDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(productRepository).existsById(1L);
        verify(inboundOrderRepository).existsById(1L);
        verify(inboundItemRepository).save(entity);
    }

    @Test
    void createInboundItem_ShouldThrowException_WhenProductNotFound() {
        CreateInboundItemDto inputDto = new CreateInboundItemDto(1L, 1L, 10);

        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundItemService.createInboundItem(inputDto));
        verify(productRepository).existsById(1L);
        verifyNoInteractions(inboundOrderRepository, inboundItemRepository);
    }

    @Test
    void createInboundItem_ShouldThrowException_WhenOrderNotFound() {
        CreateInboundItemDto inputDto = new CreateInboundItemDto(1L, 1L, 10);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(inboundOrderRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inboundItemService.createInboundItem(inputDto));
        verify(productRepository).existsById(1L);
        verify(inboundOrderRepository).existsById(1L);
        verifyNoInteractions(inboundItemRepository);
    }

    @Test
    void updateInboundItem_ShouldUpdateAndReturnDto_WhenValid() {
        Long itemId = 1L;
        InboundItemDto updateDto = new InboundItemDto(itemId, 1L, 1L, 20);
        InboundItem existingItem = new InboundItem();

        when(inboundItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(productRepository.existsById(1L)).thenReturn(true);
        when(inboundOrderRepository.existsById(1L)).thenReturn(true);
        when(productRepository.getReferenceById(1L)).thenReturn(new Product());
        when(inboundOrderRepository.getReferenceById(1L)).thenReturn(new InboundOrder());
        when(inboundItemRepository.save(existingItem)).thenReturn(existingItem);
        when(inboundItemMapper.toDto(existingItem)).thenReturn(updateDto);

        InboundItemDto result = inboundItemService.updateInboundItem(itemId, updateDto);

        assertNotNull(result);
        assertEquals(updateDto, result);
        verify(inboundItemRepository).findById(itemId);
        verify(productRepository).existsById(1L);
        verify(inboundOrderRepository).existsById(1L);
        verify(inboundItemRepository).save(existingItem);
    }

    @Test
    void updateInboundItem_ShouldThrowException_WhenItemNotFound() {
        Long itemId = 1L;
        InboundItemDto updateDto = new InboundItemDto(itemId, 1L, 1L, 20);

        when(inboundItemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inboundItemService.updateInboundItem(itemId, updateDto));
        verify(inboundItemRepository).findById(itemId);
        verifyNoInteractions(productRepository, inboundOrderRepository);
    }
}