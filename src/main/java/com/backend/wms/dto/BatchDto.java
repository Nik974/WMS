package com.backend.wms.dto;

import java.time.LocalDate;

public record BatchDto(
        Long batchId,
        Long productId,
        String lotNumber,
        LocalDate expiryDate,
        Long supplierId
) {
}
