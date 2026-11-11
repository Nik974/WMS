package com.backend.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdjustStockRequest(
        @NotNull
        Long locationId,

        @NotNull
        Long productId,

        @NotNull
        Long batchId,

        @NotNull
        @Min(0)
        Integer newQuantity,

        @NotBlank(message = "reason is required")
        String reason
) {
}
