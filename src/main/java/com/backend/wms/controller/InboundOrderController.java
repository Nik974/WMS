package com.backend.wms.controller;

import com.backend.wms.dto.CreateInboundOrderDto;
import com.backend.wms.dto.InboundOrderDto;
import com.backend.wms.entity.InboundStatus;
import com.backend.wms.service.InboundOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inbound-orders")
public class InboundOrderController {

    private final InboundOrderService inboundOrderService;

    @GetMapping
    public ResponseEntity<List<InboundOrderDto>> listAll() {
        return ResponseEntity.ok(inboundOrderService.getAllInboundOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InboundOrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inboundOrderService.getInboundOrderById(id));
    }

    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<List<InboundOrderDto>> bySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(inboundOrderService.getInboundOrdersBySupplierId(supplierId));
    }

    @GetMapping("/by-warehouse/{warehouseId}")
    public ResponseEntity<List<InboundOrderDto>> byWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inboundOrderService.getInboundOrdersByWarehouseId(warehouseId));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<InboundOrderDto>> byStatus(@PathVariable InboundStatus status) {
        return ResponseEntity.ok(inboundOrderService.getInboundOrdersByStatus(status));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<InboundOrderDto>> byDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(inboundOrderService.getInboundOrdersByCreatedBetween(start, end));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InboundOrderDto> create(@Valid @RequestBody CreateInboundOrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inboundOrderService.createInboundOrder(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InboundOrderDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody InboundOrderDto dto) {
        return ResponseEntity.ok(inboundOrderService.editInboundOrder(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InboundOrderDto> changeStatus(@PathVariable Long id,
                                                        @RequestParam InboundStatus status) {
        return ResponseEntity.ok(inboundOrderService.changeStatus(id, status));
    }
}
