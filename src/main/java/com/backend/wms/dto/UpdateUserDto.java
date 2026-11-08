package com.backend.wms.dto;

public record UpdateUserDto(
        String username,
        String email,
        String role
) {}
