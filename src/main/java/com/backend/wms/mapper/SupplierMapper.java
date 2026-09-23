package com.backend.wms.mapper;

import com.backend.wms.dto.SupplierDto;
import com.backend.wms.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    @Mapping(source = "id", target = "supplierId")
    SupplierDto toDto(Supplier entity);

    List<SupplierDto> toDtoList(List<Supplier> entities);

}
