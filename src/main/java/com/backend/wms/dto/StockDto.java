package com.backend.wms.dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record StockDto(

        Long stockId,
        Long productId,
        String productName,
        String productSku,
        Long locationId,
        String locationCode,
        Integer quantity,
        Integer reservedQuantity,
        Integer available,
        Long batchId,
        String lotNumber,
        LocalDate expiryDate
) {}
