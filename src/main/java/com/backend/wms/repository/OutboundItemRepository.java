package com.backend.wms.repository;

import com.backend.wms.entity.OutboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboundItemRepository extends JpaRepository<OutboundItem, Long> {

    List<OutboundItem> findAllByOrderId(Long orderId);
    List<OutboundItem> findAllByProductId(Long productId);
}
