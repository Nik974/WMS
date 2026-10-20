package com.backend.wms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record RegisterRequest(
        @NotBlank(message = "username cannot be null")
        @Size(min = 3)
        String username,

        @Email
        @NotBlank(message = "email cannot be null")
        String email,

        @NotBlank(message = "role cannot be null")
        String role,

        @NotBlank
        String password,
        LocalDateTime createdAt
) {}
