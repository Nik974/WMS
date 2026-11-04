package com.backend.wms.controller;

import com.backend.wms.dto.LocationDto;
import com.backend.wms.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocationsByWarehouseId(@RequestParam Long warehouseId){
        return ResponseEntity.ok(locationService.getAllLocationsByWarehouseId(warehouseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody LocationDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.addLocation(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LocationDto> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationDto dto){
        return ResponseEntity.ok(locationService.updateLocation(id, dto));
    }


}
