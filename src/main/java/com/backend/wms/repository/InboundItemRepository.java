package com.backend.wms.repository;

import com.backend.wms.entity.InboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboundItemRepository extends JpaRepository<InboundItem, Long> {
    List<InboundItem> findAllByOrderId(Long orderId);
    List<InboundItem> findAllByProductId(Long productId);
}
