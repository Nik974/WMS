package com.backend.wms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.wms.dto.CreateStockMovementDto;
import com.backend.wms.dto.StockMovementDto;
import com.backend.wms.entity.Location;
import com.backend.wms.entity.Product;
import com.backend.wms.entity.StockMovement;
import com.backend.wms.entity.User;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.StockMovementMapper;
import com.backend.wms.repository.LocationRepository;
import com.backend.wms.repository.ProductRepository;
import com.backend.wms.repository.StockMovementRepository;
import com.backend.wms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StockMovementsServiceTest {

    @InjectMocks
    private StockMovementsService stockMovementsService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private StockMovementMapper stockMovementMapper;

    @Test
    void createStockMovement_ShouldSaveAndReturnDto_WhenValid() {
        CreateStockMovementDto inputDto = new CreateStockMovementDto(1L, 1L, 2L, 1L, "TRANSFER", 10);
        StockMovement entity = new StockMovement();
        StockMovement savedEntity = new StockMovement();
        StockMovementDto expectedDto = new StockMovementDto(1L, 1L, 1L, 2L, "LOC2", 1L, "user", "TRANSFER", 10, LocalDateTime.now());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(locationRepository.existsById(1L)).thenReturn(true);
        when(locationRepository.existsById(2L)).thenReturn(true);
        when(stockMovementMapper.toEntity(inputDto)).thenReturn(entity);
        when(stockMovementRepository.save(entity)).thenReturn(savedEntity);
        when(stockMovementMapper.toDto(savedEntity)).thenReturn(expectedDto);

        StockMovementDto result = stockMovementsService.createStockMovement(inputDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userRepository).existsById(1L);
        verify(productRepository).existsById(1L);
        verify(locationRepository).existsById(1L);
        verify(locationRepository).existsById(2L);
        verify(stockMovementRepository).save(entity);
    }

    @Test
    void createStockMovement_ShouldThrowException_WhenUserNotFound() {
        CreateStockMovementDto inputDto = new CreateStockMovementDto(1L, 1L, 2L, 1L, "TRANSFER", 10);

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> stockMovementsService.createStockMovement(inputDto));
        verify(userRepository).existsById(1L);
        verifyNoInteractions(productRepository, locationRepository, stockMovementRepository);
    }

    @Test
    void createStockMovement_ShouldThrowException_WhenProductNotFound() {
        CreateStockMovementDto inputDto = new CreateStockMovementDto(1L, 1L, 2L, 1L, "TRANSFER", 10);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> stockMovementsService.createStockMovement(inputDto));
        verify(productRepository).existsById(1L);
        verifyNoInteractions(stockMovementRepository);
    }

    @Test
    void createStockMovement_ShouldThrowException_WhenToLocationNotFound() {
        CreateStockMovementDto inputDto = new CreateStockMovementDto(1L, 1L, 2L, 1L, "TRANSFER", 10);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(locationRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> stockMovementsService.createStockMovement(inputDto));
        verify(locationRepository).existsById(2L);
        verifyNoInteractions(stockMovementRepository);
    }

    @Test
    void createStockMovement_ShouldThrowException_WhenFromLocationNotFound() {
        CreateStockMovementDto inputDto = new CreateStockMovementDto(1L, 1L, 2L, 1L, "TRANSFER", 10);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(locationRepository.existsById(2L)).thenReturn(true);
        when(locationRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> stockMovementsService.createStockMovement(inputDto));
        verify(locationRepository).existsById(1L);
        verifyNoInteractions(stockMovementRepository);
    }
    @Test
    void editStockMovement_ShouldUpdateAndReturnDto_WhenValid() {
        Long movementId = 1L;
        CreateStockMovementDto updateDto = new CreateStockMovementDto(1L, 1L, 2L, 1L, "TRANSFER", 15);
        StockMovement existingMovement = new StockMovement();
        StockMovementDto expectedDto = new StockMovementDto(movementId, 1L, 1L, 2L, "LOC2", 1L, "user", "TRANSFER", 15, LocalDateTime.now());

        when(stockMovementRepository.findById(movementId)).thenReturn(Optional.of(existingMovement));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(locationRepository.existsById(1L)).thenReturn(true);
        when(locationRepository.existsById(2L)).thenReturn(true);
        when(userRepository.getReferenceById(1L)).thenReturn(new User());
        when(productRepository.getReferenceById(1L)).thenReturn(new Product());
        when(locationRepository.getReferenceById(1L)).thenReturn(new Location());
        when(locationRepository.getReferenceById(2L)).thenReturn(new Location());
        when(stockMovementRepository.save(existingMovement)).thenReturn(existingMovement);
        when(stockMovementMapper.toDto(existingMovement)).thenReturn(expectedDto);

        StockMovementDto result = stockMovementsService.editStockMovement(movementId, updateDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(stockMovementRepository).findById(movementId);
        verify(stockMovementRepository).save(existingMovement);
    }

    @Test
    void editStockMovement_ShouldThrowException_WhenMovementNotFound() {
        Long movementId = 1L;
        CreateStockMovementDto updateDto = new CreateStockMovementDto(1L, 1L, 2L, 1L, "TRANSFER", 15);

        when(stockMovementRepository.findById(movementId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> stockMovementsService.editStockMovement(movementId, updateDto));
        verify(stockMovementRepository).findById(movementId);
        verifyNoInteractions(userRepository, productRepository, locationRepository);
    }

    @Test
    void editStockMovement_ShouldThrowException_WhenUserNotFound() {
        Long movementId = 1L;
        CreateStockMovementDto updateDto = new CreateStockMovementDto(1L, 1L, 2L, 1L, "TRANSFER", 15);
        StockMovement existingMovement = new StockMovement();

        when(stockMovementRepository.findById(movementId)).thenReturn(Optional.of(existingMovement));
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> stockMovementsService.editStockMovement(movementId, updateDto));
        verify(userRepository).existsById(1L);
        verifyNoInteractions(productRepository, locationRepository);
    }
}