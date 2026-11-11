package com.backend.wms.controller;

import com.backend.wms.dto.AdjustStockRequest;
import com.backend.wms.dto.StockDto;
import com.backend.wms.entity.User;
import com.backend.wms.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockDto>> list(@RequestParam(required = false) Long productId,
                                               @RequestParam(required = false) Long warehouseId) {
        return ResponseEntity.ok(stockService.getStocks(productId, warehouseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @PostMapping("/adjustments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<StockDto> adjust(@Valid @RequestBody AdjustStockRequest request,
                                           @AuthenticationPrincipal User current) {
        return ResponseEntity.ok(stockService.adjust(request, current.getId()));
    }
}
