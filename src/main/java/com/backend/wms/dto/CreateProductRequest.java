package com.backend.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank(message = "product name is required")
        String productName,

        @NotBlank(message = "SKU is required")
        String productSku,

        String unit,

        @NotNull(message = "category id is required")
        Long categoryId
) {}
