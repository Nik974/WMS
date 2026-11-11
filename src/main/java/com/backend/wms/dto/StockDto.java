package com.backend.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record StockDto(

        Long stockId,

        @NotNull(message = "Product ID is required")
        Long productId,
        String productName,
        String productSku,

        @NotNull(message = "Location ID is required")
        Long locationId,
        String locationCode,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be a positive integer")
        Integer quantity,

        @NotNull(message = "Reserved quantity is required")
        @Min(value = 0, message = "Reserved quantity must be a non-negative integer")
        Integer reservedQuantity,
        Integer available,

        @NotNull(message = "Batch ID is required")
        Long batchId,
        String lotNumber,
        LocalDate expiryDate
) {}
