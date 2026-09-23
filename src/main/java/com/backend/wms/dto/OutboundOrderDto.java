package com.backend.wms.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OutboundOrderDto(
        Long orderId,
        Long warehouseId,
        Long customerId,
        String customerName,
        String status,
        LocalDateTime createdAt,
        List<OutboundItemDto> items

) {}
