package com.backend.wms.repository;

import com.backend.wms.entity.Stock;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Override
    @EntityGraph(attributePaths = {"location", "product", "batch"})
    Optional<Stock> findById(Long id);

    @EntityGraph(attributePaths = {"location", "product", "batch"})
    List<Stock> findAllByProductId(Long productId);
}
