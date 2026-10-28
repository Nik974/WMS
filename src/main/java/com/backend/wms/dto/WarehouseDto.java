package com.backend.wms.dto;

import jakarta.validation.constraints.NotBlank;

public record WarehouseDto(
        Long id,
        @NotBlank(message = "warehouse name is required")
        String warehouseName,
        @NotBlank(message = "warehouse name is required")
        String warehouseAddress
) {}
