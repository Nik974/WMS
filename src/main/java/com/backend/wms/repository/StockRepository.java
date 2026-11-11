package com.backend.wms.repository;

import com.backend.wms.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Override
    @EntityGraph(attributePaths = {"location", "product", "batch"})
    Optional<Stock> findById(Long id);

    @EntityGraph(attributePaths = {"location", "product", "batch"})
    List<Stock> findAllByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select s from Stock s
            where s.product.id = :productId
              and s.location.warehouse.id = :warehouseId
              and s.quantity - s.reservedQuantity > 0
              and (s.batch.expiryDate is null or s.batch.expiryDate >= :today)
            order by s.batch.expiryDate asc nulls last, s.id asc
            """)
    List<Stock> findAvailableForShipment(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId,
                                         @Param("today") LocalDate today);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select s from Stock s
            where s.location.id = :locationId and s.product.id = :productId and s.batch.id = :batchId
            """)
    Optional<Stock> findForUpdate(@Param("locationId") Long locationId, @Param("productId") Long productId,
                                  @Param("batchId") Long batchId);

    @Query("""
            select s from Stock s
            where (:productId is null or s.product.id = :productId)
              and (:warehouseId is null or s.location.warehouse.id = :warehouseId)
            """)
    List<Stock> search(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);
}
