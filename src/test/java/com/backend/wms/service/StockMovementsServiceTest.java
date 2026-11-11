package com.backend.wms.service;

import com.backend.wms.dto.StockMovementDto;
import com.backend.wms.entity.StockMovement;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.StockMovementMapper;
import com.backend.wms.repository.LocationRepository;
import com.backend.wms.repository.ProductRepository;
import com.backend.wms.repository.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StockMovementsServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private StockMovementMapper stockMovementMapper;

    @InjectMocks
    private StockMovementsService stockMovementsService;

    @Test
    void findAllMovements() {
        StockMovement movement = new StockMovement();
        StockMovementDto dto = new StockMovementDto(1L, 1L, "Product", 1L, "LOC1", 2L, "LOC2", 1L, "LOT", 1L, "User", "RECEIPT", 10, "REF", 1L, "Comment", LocalDateTime.now());

        when(stockMovementRepository.findTop500ByOrderByCreatedAtDesc()).thenReturn(List.of(movement));
        when(stockMovementMapper.toDtoList(List.of(movement))).thenReturn(List.of(dto));

        List<StockMovementDto> result = stockMovementsService.findAllMovements();

        assertThat(result).hasSize(1).contains(dto);
        verify(stockMovementRepository).findTop500ByOrderByCreatedAtDesc();
    }

    @Test
    void findAllStockMovementsByProductId_Success() {
        Long productId = 1L;
        StockMovement movement = new StockMovement();
        StockMovementDto dto = new StockMovementDto(1L, productId, "Product", 1L, "LOC1", 2L, "LOC2", 1L, "LOT", 1L, "User", "RECEIPT", 10, "REF", 1L, "Comment", LocalDateTime.now());

        when(productRepository.existsById(productId)).thenReturn(true);
        when(stockMovementRepository.findAllByProductId(productId)).thenReturn(List.of(movement));
        when(stockMovementMapper.toDtoList(List.of(movement))).thenReturn(List.of(dto));

        List<StockMovementDto> result = stockMovementsService.findAllStockMovementsByProductId(productId);

        assertThat(result).hasSize(1).contains(dto);
        verify(stockMovementRepository).findAllByProductId(productId);
    }

    @Test
    void findAllStockMovementsByProductId_NotFound() {
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(false);

        assertThatThrownBy(() -> stockMovementsService.findAllStockMovementsByProductId(productId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findAllByMovementType() {
        String type = "SHIPMENT";
        StockMovement movement = new StockMovement();
        StockMovementDto dto = new StockMovementDto(1L, 1L, "Product", 1L, "LOC1", 2L, "LOC2", 1L, "LOT", 1L, "User", type, 10, "REF", 1L, "Comment", LocalDateTime.now());

        when(stockMovementRepository.findAllByMovementType(type)).thenReturn(List.of(movement));
        when(stockMovementMapper.toDtoList(List.of(movement))).thenReturn(List.of(dto));

        List<StockMovementDto> result = stockMovementsService.findAllByMovementType(type);

        assertThat(result).hasSize(1).contains(dto);
        verify(stockMovementRepository).findAllByMovementType(type);
    }

    @Test
    void findAllByTimeAndDateBeforeCreated() {
        LocalDateTime time = LocalDateTime.now();
        StockMovement movement = new StockMovement();
        StockMovementDto dto = new StockMovementDto(1L, 1L, "Product", 1L, "LOC1", 2L, "LOC2", 1L, "LOT", 1L, "User", "RECEIPT", 10, "REF", 1L, "Comment", time);

        when(stockMovementRepository.findAllByCreatedAtBefore(time)).thenReturn(List.of(movement));
        when(stockMovementMapper.toDtoList(List.of(movement))).thenReturn(List.of(dto));

        List<StockMovementDto> result = stockMovementsService.findAllByTimeAndDateBeforeCreated(time);

        assertThat(result).hasSize(1).contains(dto);
        verify(stockMovementRepository).findAllByCreatedAtBefore(time);
    }

    @Test
    void findAllByTimeAndDateAfterCreated() {
        LocalDateTime time = LocalDateTime.now();
        StockMovement movement = new StockMovement();
        StockMovementDto dto = new StockMovementDto(1L, 1L, "Product", 1L, "LOC1", 2L, "LOC2", 1L, "LOT", 1L, "User", "RECEIPT", 10, "REF", 1L, "Comment", time);

        when(stockMovementRepository.findAllByCreatedAtAfter(time)).thenReturn(List.of(movement));
        when(stockMovementMapper.toDtoList(List.of(movement))).thenReturn(List.of(dto));

        List<StockMovementDto> result = stockMovementsService.findAllByTimeAndDateAfterCreated(time);

        assertThat(result).hasSize(1).contains(dto);
        verify(stockMovementRepository).findAllByCreatedAtAfter(time);
    }

    @Test
    void findAllByDateCreated() {
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        StockMovement movement = new StockMovement();
        StockMovementDto dto = new StockMovementDto(1L, 1L, "Product", 1L, "LOC1", 2L, "LOC2", 1L, "LOT", 1L, "User", "RECEIPT", 10, "REF", 1L, "Comment", start.plusHours(1));

        when(stockMovementRepository.findAllByCreatedAtBetween(start, end)).thenReturn(List.of(movement));
        when(stockMovementMapper.toDtoList(List.of(movement))).thenReturn(List.of(dto));

        List<StockMovementDto> result = stockMovementsService.findAllByDateCreated(date);

        assertThat(result).hasSize(1).contains(dto);
        verify(stockMovementRepository).findAllByCreatedAtBetween(start, end);
    }

    @Test
    void findAllByToLocationId_Success() {
        Long locationId = 1L;
        StockMovement movement = new StockMovement();
        StockMovementDto dto = new StockMovementDto(1L, 1L, "Product", 2L, "LOC1", locationId, "LOC2", 1L, "LOT", 1L, "User", "RECEIPT", 10, "REF", 1L, "Comment", LocalDateTime.now());

        when(locationRepository.existsById(locationId)).thenReturn(true);
        when(stockMovementRepository.findAllByToLocationId(locationId)).thenReturn(List.of(movement));
        when(stockMovementMapper.toDtoList(List.of(movement))).thenReturn(List.of(dto));

        List<StockMovementDto> result = stockMovementsService.findAllByToLocationId(locationId);

        assertThat(result).hasSize(1).contains(dto);
        verify(stockMovementRepository).findAllByToLocationId(locationId);
    }

    @Test
    void findAllByToLocationId_NotFound() {
        Long locationId = 1L;
        when(locationRepository.existsById(locationId)).thenReturn(false);

        assertThatThrownBy(() -> stockMovementsService.findAllByToLocationId(locationId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findAllByFromLocationId_Success() {
        Long locationId = 1L;
        StockMovement movement = new StockMovement();
        StockMovementDto dto = new StockMovementDto(1L, 1L, "Product", locationId, "LOC1", 2L, "LOC2", 1L, "LOT", 1L, "User", "SHIPMENT", 10, "REF", 1L, "Comment", LocalDateTime.now());

        when(locationRepository.existsById(locationId)).thenReturn(true);
        when(stockMovementRepository.findAllByFromLocationId(locationId)).thenReturn(List.of(movement));
        when(stockMovementMapper.toDtoList(List.of(movement))).thenReturn(List.of(dto));

        List<StockMovementDto> result = stockMovementsService.findAllByFromLocationId(locationId);

        assertThat(result).hasSize(1).contains(dto);
        verify(stockMovementRepository).findAllByFromLocationId(locationId);
    }

    @Test
    void findAllByFromLocationId_NotFound() {
        Long locationId = 1L;
        when(locationRepository.existsById(locationId)).thenReturn(false);

        assertThatThrownBy(() -> stockMovementsService.findAllByFromLocationId(locationId))
                .isInstanceOf(ResourceNotFoundException.class);
    }


}