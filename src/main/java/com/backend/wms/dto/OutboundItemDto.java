package com.backend.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OutboundItemDto(
        Long id,
        Long orderId,
        Long productId,
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity

) {
}
