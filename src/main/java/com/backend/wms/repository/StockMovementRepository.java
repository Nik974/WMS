package com.backend.wms.repository;

import com.backend.wms.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement,Long> {
    List<StockMovement> findAllByProductId(Long productId);

    List<StockMovement> findAllByMovementTypeIsContainingIgnoreCase(String movementType);

    List<StockMovement> findAllByCreatedAtBefore(LocalDateTime createdAtBefore);

    List<StockMovement> findAllByCreatedAtAfter(LocalDateTime createdAtAfter);

    List<StockMovement> findAllByCreatedAt_Date(LocalDate createdAtDate);

    List<StockMovement> findAllByFromLocationId(Long fromLocationId);

    List<StockMovement> findAllByToLocationId(Long toLocationId);
}
