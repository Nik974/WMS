package com.backend.wms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserDto(
        @NotBlank @Size(min = 3) String username,
        @NotBlank @Email String email,
        @NotBlank String role
) {}
