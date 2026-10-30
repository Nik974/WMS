package com.backend.wms.dto;

import com.backend.wms.entity.OutboundStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateOutboundOrderDto(
        Long orderId,
        @NotNull
        Long warehouseId,
        @NotNull
        Long customerId,
        String customerName,
        OutboundStatus status,
        LocalDateTime createdAt,
        @NotEmpty
        @Valid
        List<OutboundItemDto> items
) {
}
