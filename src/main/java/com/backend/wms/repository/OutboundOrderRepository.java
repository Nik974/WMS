package com.backend.wms.repository;

import com.backend.wms.entity.OutboundOrder;
import com.backend.wms.entity.OutboundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long> {

    List<OutboundOrder> findAllByCustomerId(Long customerId);

    List<OutboundOrder> findAllByWarehouseId(Long warehouseId);

    List<OutboundOrder> findAllByStatus(OutboundStatus status);

    List<OutboundOrder> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
