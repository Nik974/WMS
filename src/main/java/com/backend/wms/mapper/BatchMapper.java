package com.backend.wms.mapper;


import com.backend.wms.dto.BatchDto;
import com.backend.wms.entity.Batch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(source = "id", target = "batchId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "supplier.id", target = "supplierId")

    BatchDto toDto(Batch entity);

    List<BatchDto> toDtoList(List<Batch> entities);

}
