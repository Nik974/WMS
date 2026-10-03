package com.backend.wms.mapper;


import com.backend.wms.dto.BatchDto;
import com.backend.wms.entity.Batch;
import com.backend.wms.entity.Product;
import com.backend.wms.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(source = "id", target = "batchId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "supplier.id", target = "supplierId")
    BatchDto toDto(Batch entity);

    List<BatchDto> toDtoList(List<Batch> entities);

    @Mapping(target = "id", ignore = true)
    Batch toEntity(BatchDto dto, Product product, Supplier supplier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "supplier", source = "supplier")
    void updateEntity(BatchDto dto, Supplier supplier, @MappingTarget Batch entity);

}
