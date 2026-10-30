package com.backend.wms.controller;

import com.backend.wms.dto.CreateOutboundOrderDto;
import com.backend.wms.dto.OutboundOrderDto;
import com.backend.wms.entity.OutboundStatus;
import com.backend.wms.service.OutboundOrderService;
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
@RequestMapping("/api/v1/outbound-orders")
public class OutboundOrderController {

    private final OutboundOrderService outboundOrderService;

    @GetMapping
    public ResponseEntity<List<OutboundOrderDto>> listAll() {
        return ResponseEntity.ok(outboundOrderService.getAllOutboundOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutboundOrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(outboundOrderService.getOutboundOrderById(id));
    }

    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<OutboundOrderDto>> byCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(outboundOrderService.getOutboundOrdersByCustomerId(customerId));
    }

    @GetMapping("/by-warehouse/{warehouseId}")
    public ResponseEntity<List<OutboundOrderDto>> byWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(outboundOrderService.getOutboundOrdersByWarehouseId(warehouseId));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<OutboundOrderDto>> byStatus(@PathVariable OutboundStatus status) {
        return ResponseEntity.ok(outboundOrderService.getOutboundOrdersByStatus(status));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<OutboundOrderDto>> byDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(outboundOrderService.getOutboundOrdersByCreatedAtBetween(start, end));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OutboundOrderDto> create(@Valid @RequestBody CreateOutboundOrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(outboundOrderService.createOutboundOrder(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OutboundOrderDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody OutboundOrderDto dto) {
        return ResponseEntity.ok(outboundOrderService.editOutboundOrder(id, dto));
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OutboundOrderDto> changeStatus(@PathVariable Long id,
                                                         @RequestParam OutboundStatus status) {
        return ResponseEntity.ok(outboundOrderService.changeStatus(id, status));
    }
}
