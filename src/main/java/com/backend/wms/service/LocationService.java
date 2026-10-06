package com.backend.wms.service;

import com.backend.wms.dto.LocationDto;
import com.backend.wms.entity.Location;
import com.backend.wms.entity.Warehouse;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.LocationMapper;
import com.backend.wms.repository.LocationRepository;
import com.backend.wms.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public LocationDto addLocation(LocationDto locationDto) {
        Warehouse warehouse = warehouseRepository.findById(locationDto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        Location location = locationMapper.toEntity(locationDto);
        location.setWarehouse(warehouse);
        Location savedLocation = locationRepository.save(location);
        return locationMapper.toDto(savedLocation);
    }

    @Transactional
    public LocationDto updateLocation(LocationDto locationDto){
        Location location = locationRepository.findById(locationDto.locationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        if(warehouseRepository.findById(locationDto.warehouseId()).isEmpty()){
            throw new ResourceNotFoundException("Warehouse not found");
        }
        Warehouse warehouse = warehouseRepository.getReferenceById(locationDto.warehouseId());
        location.setWarehouse(warehouse);
        location.setZone(locationDto.zone());
        location.setAisle(locationDto.aisle());
        location.setRack(locationDto.rack());
        location.setShelf(locationDto.shelf());
        location.setBin(locationDto.bin());
        location.setCapacity(locationDto.capacity());
        location.setCode(locationDto.code());

        return locationMapper.toDto(location);
    }

    public List<LocationDto> getAllLocationsByWarehouseId(Long warehouseId) {
        return locationRepository.findAllByWarehouseId(warehouseId).stream()
                .map(locationMapper::toDto)
                .toList();
    }

}
