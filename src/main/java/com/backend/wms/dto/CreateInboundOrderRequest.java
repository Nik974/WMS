package com.backend.wms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateInboundOrderRequest(
        @NotNull(message = "warehouse id is required")
        Long warehouseId,

        @NotNull(message = "supplier id is required")
        Long supplierId,

        @NotEmpty(message = "order must contain at least one item")
        @Valid
        List<ItemRequest> items
) {
    public record ItemRequest(

            @NotNull(message = "product id is required")
            Long productId,

            @Positive(message = "quantity must be greater than 0")
            Integer quantity

    ) {}
}
