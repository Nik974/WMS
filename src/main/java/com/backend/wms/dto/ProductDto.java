package com.backend.wms.dto;

public record ProductDto(

        Long productId,
        String productName,
        String productSku,
        String unit,
        Long categoryId,
        String categoryName

) {}
