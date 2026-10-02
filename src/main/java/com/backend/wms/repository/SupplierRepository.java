package com.backend.wms.repository;

import com.backend.wms.entity.Supplier;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findAllBySupplierNameContainingIgnoreCase(String supplierName, Sort sort);

    Optional<Supplier> findByTaxId(String taxId);
    boolean existsByTaxId(String taxId);
    boolean existsByTaxIdAndIdNot(String taxId, Long id);

}
