package com.backend.wms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.wms.dto.LocationDto;
import com.backend.wms.entity.Location;
import com.backend.wms.entity.Warehouse;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.LocationMapper;
import com.backend.wms.repository.LocationRepository;
import com.backend.wms.repository.WarehouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private LocationService locationService;

    private LocationDto createLocationDto(Long locationId, Long warehouseId) {
        return new LocationDto(
                locationId,
                warehouseId,
                "Zone-A",
                "Aisle-1",
                "Rack-B",
                "Shelf-2",
                "Bin-05",
                100,
                "LOC-A1B2"
        );
    }

    private Location createLocation(Long locationId) {
        Location location = new Location();
        location.setId(locationId);
        location.setZone("Zone-A");
        location.setAisle("Aisle-1");
        location.setRack("Rack-B");
        location.setShelf("Shelf-2");
        location.setBin("Bin-05");
        location.setCapacity(100);
        location.setCode("LOC-A1B2");
        return location;
    }

    @Nested
    class AddLocationTests {

        @Test
        void addLocation_Success() {
            Long warehouseId = 10L;
            LocationDto inputDto = createLocationDto(null, warehouseId);
            Warehouse mockWarehouse = new Warehouse();
            mockWarehouse.setId(warehouseId);

            Location unmappedLocation = createLocation(null);
            Location savedLocation = createLocation(1L);
            savedLocation.setWarehouse(mockWarehouse);

            LocationDto expectedDto = createLocationDto(1L, warehouseId);

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(mockWarehouse));
            when(locationMapper.toEntity(inputDto)).thenReturn(unmappedLocation);
            when(locationRepository.save(unmappedLocation)).thenReturn(savedLocation);
            when(locationMapper.toDto(savedLocation)).thenReturn(expectedDto);

            LocationDto result = locationService.addLocation(inputDto);

            assertThat(result).isNotNull();
            assertThat(result.locationId()).isEqualTo(1L);
            assertThat(unmappedLocation.getWarehouse()).isEqualTo(mockWarehouse);

            verify(warehouseRepository, times(1)).findById(warehouseId);
            verify(locationRepository, times(1)).save(unmappedLocation);
        }

        @Test
        void addLocation_WarehouseNotFound_ThrowsException() {
            Long warehouseId = 99L;
            LocationDto inputDto = createLocationDto(null, warehouseId);

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> locationService.addLocation(inputDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Warehouse not found");

            verify(locationRepository, never()).save(any());
        }
    }


    @Test
    void updateLocation_Success() {
        Long locationId = 1L;
        Long warehouseId = 10L;
        LocationDto updateDto = createLocationDto(locationId, warehouseId);

        Location existingLocation = createLocation(locationId);
        Warehouse mockWarehouseProxy = new Warehouse();
        mockWarehouseProxy.setId(warehouseId);

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(existingLocation));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(mockWarehouseProxy));
        when(warehouseRepository.getReferenceById(warehouseId)).thenReturn(mockWarehouseProxy);
        when(locationMapper.toDto(existingLocation)).thenReturn(updateDto);

        LocationDto result = locationService.updateLocation(updateDto);

        assertThat(result).isNotNull();
        assertThat(existingLocation.getWarehouse()).isEqualTo(mockWarehouseProxy);
        assertThat(existingLocation.getZone()).isEqualTo(updateDto.zone());

        verify(locationRepository, times(1)).findById(locationId);
        verify(warehouseRepository, times(1)).getReferenceById(warehouseId);
    }

    @Test
    void updateLocation_LocationNotFound_ThrowsException() {
        LocationDto updateDto = createLocationDto(99L, 10L);

        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.updateLocation(updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Location not found");

        verify(warehouseRepository, never()).getReferenceById(any());
    }

    @Test
    void updateLocation_WarehouseNotFound_ThrowsException() {
        Long locationId = 1L;
        Long warehouseId = 99L;
        LocationDto updateDto = createLocationDto(locationId, warehouseId);
        Location existingLocation = createLocation(locationId);

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(existingLocation));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.updateLocation(updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Warehouse not found");

        verify(warehouseRepository, never()).getReferenceById(any());
    }


    @Test
    void getAllLocationsByWarehouseId_Success() {
        Long warehouseId = 10L;
        Location loc1 = createLocation(1L);
        Location loc2 = createLocation(2L);

        LocationDto dto1 = createLocationDto(1L, warehouseId);
        LocationDto dto2 = createLocationDto(2L, warehouseId);

        when(locationRepository.findAllByWarehouseId(warehouseId)).thenReturn(List.of(loc1, loc2));
        when(locationMapper.toDto(loc1)).thenReturn(dto1);
        when(locationMapper.toDto(loc2)).thenReturn(dto2);

        List<LocationDto> result = locationService.getAllLocationsByWarehouseId(warehouseId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);

        verify(locationRepository, times(1)).findAllByWarehouseId(warehouseId);
    }

    @Test
    void getAllLocationsByWarehouseId_EmptyResult() {
        Long warehouseId = 10L;
        when(locationRepository.findAllByWarehouseId(warehouseId)).thenReturn(List.of());

        List<LocationDto> result = locationService.getAllLocationsByWarehouseId(warehouseId);

        assertThat(result).isEmpty();
        verify(locationRepository, times(1)).findAllByWarehouseId(warehouseId);
        verifyNoInteractions(locationMapper);
    }

}