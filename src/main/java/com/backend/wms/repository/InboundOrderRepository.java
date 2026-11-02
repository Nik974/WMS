package com.backend.wms.repository;

import com.backend.wms.entity.InboundOrder;
import com.backend.wms.entity.InboundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long> {

    List<InboundOrder> findAllByWarehouseId(Long warehouseId);

    List<InboundOrder> findAllByStatus(InboundStatus status);

    List<InboundOrder> findAllBySupplierId(Long supplierId);

    List<InboundOrder> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
