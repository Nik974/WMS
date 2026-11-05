package com.backend.wms.repository;

import com.backend.wms.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findAllByProductId(Long productId);

    List<Batch> findByLotNumber(String lotNumber);

    List<Batch> findAllByExpiryDateBefore(LocalDate date);

    boolean existsByProductIdAndLotNumber(Long productId, String lotNumber);

    boolean existsByProductIdAndLotNumberAndIdNot(Long productId, String lotNumber, Long id);
}
