package com.backend.wms.dto;

import com.backend.wms.entity.InboundStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateInboundOrderDto(
        @NotNull(message = "warehouse id cannot be null")
        Long warehouseId,
        @NotNull(message = "supplier id cannot be null")
        Long supplierId,
        String supplierName,
        InboundStatus status,
        LocalDateTime createdAt,
        @NotEmpty(message = "order must contain at least one item")
        @Valid
        List<InboundItemDto> items
) {}
