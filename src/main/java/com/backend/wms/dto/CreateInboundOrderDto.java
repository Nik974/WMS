package com.backend.wms.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CreateInboundOrderDto(

        Long warehouseId,
        Long supplierId,
        String supplierName,
        String status,
        LocalDateTime createdAt,
        List<InboundItemDto> items
) {
}
