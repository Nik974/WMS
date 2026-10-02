package com.backend.wms.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateSupplierDto(

        @NotBlank(message = "Supplier name cannot be blank")
        String supplierName,

        @NotBlank(message = "Tax ID cannot be blank")
        String taxId
) {
}
