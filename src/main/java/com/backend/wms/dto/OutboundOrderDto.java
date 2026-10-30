package com.backend.wms.dto;

import com.backend.wms.entity.OutboundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record OutboundOrderDto(
        Long id,
        Long orderId,
        @NotNull(message = "warehouse id can not be empty")
        Long warehouseId,
        @NotNull(message = "customer id can not be empty")
        Long customerId,
        String customerName,
        OutboundStatus status,
        LocalDateTime createdAt,
        List<OutboundItemDto> items
) {}
