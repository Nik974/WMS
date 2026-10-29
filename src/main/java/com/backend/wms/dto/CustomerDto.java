package com.backend.wms.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerDto(
        Long customerId, 
        @NotBlank(message = "Customer's name is required")
        String customerName
){}
