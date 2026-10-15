package com.backend.wms.dto;

public record InboundItemDto(
        Long id,
        Long orderId,
        Long productId,
        Integer quantity
) {}
