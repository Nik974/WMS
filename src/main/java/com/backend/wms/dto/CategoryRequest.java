package com.backend.wms.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(

        @NotBlank(message = "category name is required")
        String categoryName
) {}
