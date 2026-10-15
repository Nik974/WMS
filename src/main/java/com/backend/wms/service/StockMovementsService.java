package com.backend.wms.service;

import com.backend.wms.dto.CreateStockMovementDto;
import com.backend.wms.dto.StockMovementDto;
import com.backend.wms.entity.StockMovement;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.StockMovementMapper;
import com.backend.wms.repository.LocationRepository;
import com.backend.wms.repository.ProductRepository;
import com.backend.wms.repository.StockMovementRepository;
import com.backend.wms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockMovementsService {

    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;

    public List<StockMovementDto> findAllMovements(){
        return stockMovementMapper.toDtoList(stockMovementRepository.findAll());
    }

    public List<StockMovementDto> findAllStockMovementsByProductId(Long id){
        if (!productRepository.existsById(id)){
            throw new ResourceNotFoundException("No product with id: " + id);
        }
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByProductId(id));
    }

    public List<StockMovementDto> findAllByMovementType(String movementType){
        return stockMovementMapper.toDtoList(stockMovementRepository
                .findAllByMovementTypeIsContainingIgnoreCase(movementType));
    }

    public List<StockMovementDto> findAllByTimeAndDateBeforeCreated(LocalDateTime localDateTime){
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByCreatedAtBefore(localDateTime));
    }

    public List<StockMovementDto> findAllByTimeAndDateAfterCreated(LocalDateTime localDateTime){
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByCreatedAtAfter(localDateTime));
    }

    public List<StockMovementDto> findAllByDateCreated(LocalDate date){
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByCreatedAt_Date(date));
    }

    public List<StockMovementDto> findAllByToLocationId(Long id){
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("No 'TO' location with id: " + id);
        }
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByToLocationId(id));
    }

    public List<StockMovementDto> findAllByFromLocationId(Long id){
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("No 'FROM' location with id: " + id);
        }
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByFromLocationId(id));
    }


    @Transactional
    public StockMovementDto createStockMovement(CreateStockMovementDto dto){
        if(!userRepository.existsById(dto.userId())){
            throw new ResourceNotFoundException("No user with id: " + dto.userId());
        }
        if (!productRepository.existsById(dto.productId())){
            throw new ResourceNotFoundException("No product with id: " + dto.productId());
        }
        if (!locationRepository.existsById(dto.toLocationId())){
            throw new ResourceNotFoundException("No 'TO' location with id: " + dto.toLocationId());
        }
        if (!locationRepository.existsById(dto.fromLocationId())){
            throw new ResourceNotFoundException("No 'FROM' location with id: " + dto.fromLocationId());
        }

        StockMovement stockMovement = stockMovementMapper.toEntity(dto);
        stockMovement.setCreatedAt(LocalDateTime.now());
        StockMovement savedStockMovement = stockMovementRepository.save(stockMovement);
        return stockMovementMapper.toDto(savedStockMovement);

    }

    @Transactional
    public StockMovementDto editStockMovement(Long id, CreateStockMovementDto dto) {
        StockMovement existingMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with id: " + id));

        if (!userRepository.existsById(dto.userId())) {
            throw new ResourceNotFoundException("No user with id: " + dto.userId());
        }
        if (!productRepository.existsById(dto.productId())) {
            throw new ResourceNotFoundException("No product with id: " + dto.productId());
        }
        if (!locationRepository.existsById(dto.fromLocationId())) {
            throw new ResourceNotFoundException("No 'FROM' location with id: " + dto.fromLocationId());
        }
        if (!locationRepository.existsById(dto.toLocationId())) {
            throw new ResourceNotFoundException("No 'TO' location with id: " + dto.toLocationId());
        }

        existingMovement.setUser(userRepository.getReferenceById(dto.userId()));
        existingMovement.setProduct(productRepository.getReferenceById(dto.productId()));
        existingMovement.setFromLocation(locationRepository.getReferenceById(dto.fromLocationId()));
        existingMovement.setToLocation(locationRepository.getReferenceById(dto.toLocationId()));
        existingMovement.setMovementType(dto.movementType());
        existingMovement.setQuantity(dto.quantity());
        StockMovement updatedMovement = stockMovementRepository.save(existingMovement);
        return stockMovementMapper.toDto(updatedMovement);
    }

}
