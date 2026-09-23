package com.backend.wms.mapper;
import com.backend.wms.dto.StockMovementDto;
import com.backend.wms.entity.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "fromLocation.id", target = "fromLocationId")
    @Mapping(source = "fromLocation.code", target = "fromLocationCode")
    @Mapping(source = "toLocation.id", target = "toLocationId")
    @Mapping(source = "toLocation.code", target = "toLocationCode")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    StockMovementDto toDto(StockMovement entity);

    List<StockMovementDto> toDtoList(List<StockMovement> entities);

}
