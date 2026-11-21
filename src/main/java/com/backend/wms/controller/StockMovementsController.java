package com.backend.wms.controller;

import com.backend.wms.dto.StockMovementDto;
import com.backend.wms.service.StockMovementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock-movements")
public class StockMovementsController {

    private final StockMovementsService stockMovementsService;

    @GetMapping
    public ResponseEntity<List<StockMovementDto>> list() {
        return ResponseEntity.ok(stockMovementsService.findAllMovements());
    }
}
