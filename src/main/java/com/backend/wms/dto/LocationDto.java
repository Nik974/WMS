package com.backend.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationDto(

        Long locationId,
        @NotNull
        Long warehouseId,
        String zone,
        String aisle,
        String rack,
        String shelf,
        String bin,
        @Min(0)
        Integer capacity,
        @NotBlank
        String code

) {}
