package com.backend.wms.dto;

public record LocationDto(

        Long locationId,
        Long warehouseId,
        String zone,
        String aisle,
        String rack,
        String shelf,
        String bin,
        Integer capacity,
        String code

) {}
