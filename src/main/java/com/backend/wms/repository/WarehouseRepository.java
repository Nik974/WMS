package com.backend.wms.repository;

import com.backend.wms.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findByNameContainingIgnoreCase(String name);

    List<Warehouse> findByAddressContainingIgnoreCase(String address);

    boolean existsByNameIgnoreCase(String name);
}
