package com.backend.wms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReceiveInboundRequest(@NotEmpty @Valid List<Line> lines) {
    public record Line(
            @NotNull
            Long itemId,

            @NotNull
            Long locationId,

            @NotNull
            Long batchId
    ) {
    }
}
