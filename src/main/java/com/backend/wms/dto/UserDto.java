package com.backend.wms.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt
) {}
