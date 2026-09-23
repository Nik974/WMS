package com.backend.wms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MoveStockRequest(
        @NotNull(message = "product id is required")
        Long productId,

        Long fromLocationId,

        Long toLocationId,

        @NotNull(message = "movement type is required")
        String movementType,

        @Positive(message = "quantity must be greater than 0")
        Integer quantity,

        @NotNull(message = "user id is required")
        Long userId

) {}
