package com.backend.wms.repository;

import com.backend.wms.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findAllByProductId(Long productId);

    Optional<Batch> findByLotNumber(String lotNumber);

    List<Batch> findAllByExpiryDateBefore(LocalDate date);

}
