package com.backend.wms.dto;

import java.time.LocalDateTime;

public record StockMovementDto(
        Long id,
        Long productId,
        String productName,
        Long fromLocationId,
        String fromLocationCode,
        Long toLocationId,
        String toLocationCode,
        Long batchId,
        String lotNumber,
        Long userId,
        String username,
        String movementType,
        Integer quantity,
        String referenceType,
        Long referenceId,
        String comment,
        LocalDateTime createdAt
) {}
