package com.backend.wms.mapper;

import com.backend.wms.dto.StockDto;
import com.backend.wms.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(source = "id", target = "stockId")
    @Mapping(source = "location.id", target = "locationId")
    @Mapping(source = "location.code", target = "locationCode")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.productSku", target = "productSku")
    @Mapping(source = "batch.id", target = "batchId")
    @Mapping(source = "batch.lotNumber", target = "lotNumber")
    @Mapping(source = "batch.expiryDate", target = "expiryDate")
    @Mapping(target = "available", expression = "java(calculateAvailable(entity))")
    StockDto toDto(Stock entity);

    List<StockDto> toDtoList(List<Stock> entities);

    default Integer calculateAvailable(Stock entity) {
        int quantity = entity.getQuantity() != null ? entity.getQuantity() : 0;
        int reserved = entity.getReservedQuantity() != null ? entity.getReservedQuantity() : 0;
        return quantity - reserved;
    }

}
