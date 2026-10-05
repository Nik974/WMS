package com.backend.wms.dto;

public record WarehouseDto(
        Long id,
        String warehouseName,
        String warehouseAddress
) {}
