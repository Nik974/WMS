package com.backend.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BatchDto(
        Long batchId,
        @NotNull(message = "productId cannot be null")
        Long productId,
        @NotBlank(message = "lotNumber cannot be blank")
        String lotNumber,
        LocalDate expiryDate,
        Long supplierId
) {
}
