package com.backend.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStockMovementDto(
        @NotNull(message = "Product ID cannot be null")
        Long productId,
        @NotNull(message = "From location ID cannot be null")
        Long fromLocationId,
        @NotNull(message = "To location ID cannot be null")
        Long toLocationId,
        @NotNull(message = "User ID cannot be null")
        Long userId,
        @NotBlank(message = "Movement type cannot be blank")
        String movementType,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity

) {
}
