package com.backend.wms.mapper;

import com.backend.wms.dto.LocationDto;
import com.backend.wms.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(source = "id", target = "locationId")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    LocationDto toDto(Location entity);

    List<LocationDto> toDtoList(List<Location> entities);

    @Mapping(source = "locationId", target = "id")
    @Mapping(source = "warehouseId", target = "warehouse.id")
    Location toEntity(LocationDto dto);
}
