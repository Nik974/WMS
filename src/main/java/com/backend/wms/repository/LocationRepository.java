package com.backend.wms.repository;

import com.backend.wms.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByWarehouseId(Long warehouse_id);
}
