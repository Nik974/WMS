package com.backend.wms.controller;

import com.backend.wms.dto.WarehouseDto;
import com.backend.wms.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<WarehouseDto>> listAllWarehouses(){
        List<WarehouseDto> list = warehouseService.getAllWarehouses();
        if (list.isEmpty()) return ResponseEntity.status(404).body(list);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/name")
    public ResponseEntity<List<WarehouseDto>> findAllWarehousesByName(@RequestBody String name){
        List<WarehouseDto> list = warehouseService.searchWarehousesByName(name);
        if (list.isEmpty()) return ResponseEntity.status(404).body(list);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/address")
    public ResponseEntity<List<WarehouseDto>> findAllWarehousesByAddress(@RequestBody String address){
        List<WarehouseDto> list = warehouseService.searchWarehousesByAddress(address);
        if (list.isEmpty()) return ResponseEntity.status(404).body(list);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WarehouseDto> addNewWarehouse(@Valid @RequestBody WarehouseDto dto){
        return ResponseEntity.status(201).body(warehouseService.createWarehouse(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WarehouseDto> editWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseDto dto){
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, dto));
    }

}
