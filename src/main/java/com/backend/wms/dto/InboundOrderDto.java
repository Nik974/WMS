package com.backend.wms.dto;

import com.backend.wms.entity.InboundStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record InboundOrderDto(
        Long orderId,
        @NotNull(message = "warehouse id cannot be null")
        Long warehouseId,
        @NotNull(message = "supplier id cannot be null")
        Long supplierId,
        String supplierName,
        InboundStatus status,
        LocalDateTime createdAt,
        List<InboundItemDto> items
) {}
