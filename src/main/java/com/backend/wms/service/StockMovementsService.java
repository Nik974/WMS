package com.backend.wms.service;

import com.backend.wms.dto.StockMovementDto;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.StockMovementMapper;
import com.backend.wms.repository.LocationRepository;
import com.backend.wms.repository.ProductRepository;
import com.backend.wms.repository.StockMovementRepository;
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
    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;

    public List<StockMovementDto> findAllMovements(){
        return stockMovementMapper.toDtoList(stockMovementRepository.findTop500ByOrderByCreatedAtDesc());
    }

    public List<StockMovementDto> findAllStockMovementsByProductId(Long id){
        if (!productRepository.existsById(id)){
            throw new ResourceNotFoundException("No product with id: " + id);
        }
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByProductId(id));
    }

    public List<StockMovementDto> findAllByMovementType(String movementType){
        return stockMovementMapper.toDtoList(stockMovementRepository
                .findAllByMovementType(movementType));
    }

    public List<StockMovementDto> findAllByTimeAndDateBeforeCreated(LocalDateTime localDateTime){
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByCreatedAtBefore(localDateTime));
    }

    public List<StockMovementDto> findAllByTimeAndDateAfterCreated(LocalDateTime localDateTime){
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByCreatedAtAfter(localDateTime));
    }

    public List<StockMovementDto> findAllByDateCreated(LocalDate date){
        return stockMovementMapper.toDtoList(stockMovementRepository.findAllByCreatedAtBetween((date.atStartOfDay()), date.plusDays(1).atStartOfDay()));
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
}
