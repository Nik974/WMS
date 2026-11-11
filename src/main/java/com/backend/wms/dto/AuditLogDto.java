package com.backend.wms.dto;

import java.time.LocalDateTime;

public record AuditLogDto(
        Long id,
        LocalDateTime createdAt,
        String username,
        String email,
        String httpMethod,
        String resource,
        Long resourceId,
        String action,
        String query
) {
}