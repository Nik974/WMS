package com.backend.wms.controller;

import com.backend.wms.dto.BatchDto;
import com.backend.wms.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/batches")
public class BatchController {

    private final BatchService batchService;

    @GetMapping
    public ResponseEntity<List<BatchDto>> listAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchDto> getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.getBatchById(id));
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<BatchDto>> listBatchesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(batchService.getBatchesByProductId(productId));
    }

    @GetMapping("/by-lot/{lotNumber}")
    public ResponseEntity<List<BatchDto>> getBatchByLotNumber(@PathVariable String lotNumber) {
        return ResponseEntity.ok(batchService.getBatchByLotNumber(lotNumber));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BatchDto> createBatch(@Valid @RequestBody BatchDto batchDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.createBatch(batchDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BatchDto> updateBatch(@PathVariable Long id,
                                                @Valid @RequestBody BatchDto batchDto) {
        return ResponseEntity.ok(batchService.updateBatch(id, batchDto));
    }

    @GetMapping("/by-expiry-date")
    public ResponseEntity<List<BatchDto>> listBatchesByExpiryDateBefore(@RequestParam LocalDate thresholdDate) {
        return ResponseEntity.ok(batchService.getBatchesByExpiryDateBefore(thresholdDate));
    }
}