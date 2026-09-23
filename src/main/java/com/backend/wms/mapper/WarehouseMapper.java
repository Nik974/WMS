package com.backend.wms.mapper;


import com.backend.wms.dto.WarehouseDto;
import com.backend.wms.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    @Mapping(source = "name", target = "warehouseName")
    @Mapping(source = "address", target = "warehouseAddress")
    WarehouseDto toDto(Warehouse entity);

    List<WarehouseDto> toDtoList(List<Warehouse> entities);

}
