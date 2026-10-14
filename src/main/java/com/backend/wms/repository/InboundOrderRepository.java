package com.backend.wms.repository;

import com.backend.wms.entity.InboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long> {

    List<InboundOrder> findAllByWarehouseId(Long warehouseId);

    List<InboundOrder> findAllByStatus(String status);

    List<InboundOrder> findAllBySupplierId(Long supplierId);

    List<InboundOrder> findTopByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
