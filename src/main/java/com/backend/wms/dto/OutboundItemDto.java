package com.backend.wms.dto;

public record OutboundItemDto(
        Long id,
        Long orderId,
        Long productId,
        String productName,
        Integer quantity

) {
}
