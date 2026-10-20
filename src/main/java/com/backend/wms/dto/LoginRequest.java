package com.backend.wms.dto;

public record LoginRequest(
        String username,
        String password
) {}