package com.backend.wms.dto;

public record AuthResponse(
        String token,
        String username,
        String role
) {}