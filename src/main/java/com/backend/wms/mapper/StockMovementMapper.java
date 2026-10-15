package com.backend.wms.mapper;

import com.backend.wms.dto.CreateStockMovementDto;
import com.backend.wms.dto.StockMovementDto;
import com.backend.wms.entity.Location;
import com.backend.wms.entity.Product;
import com.backend.wms.entity.StockMovement;
import com.backend.wms.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "fromLocation.id", target = "fromLocationId")
    @Mapping(source = "toLocation.id", target = "toLocationId")
    @Mapping(source = "toLocation.code", target = "toLocationCode")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    StockMovementDto toDto(StockMovement entity);

    List<StockMovementDto> toDtoList(List<StockMovement> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "product", expression = "java(mapProduct(dto.productId()))")
    @Mapping(target = "fromLocation", expression = "java(mapLocation(dto.fromLocationId()))")
    @Mapping(target = "toLocation", expression = "java(mapLocation(dto.toLocationId()))")
    @Mapping(target = "user", expression = "java(mapUser(dto.userId()))")
    StockMovement toEntity(CreateStockMovementDto dto);

    default Product mapProduct(Long id) {
        if (id == null) return null;
        Product product = new Product();
        product.setId(id);
        return product;
    }

    default Location mapLocation(Long id) {
        if (id == null) return null;
        Location location = new Location();
        location.setId(id);
        return location;
    }

    default User mapUser(Long id) {
        if (id == null) return null;
        User user = new User();
        user.setId(id);
        return user;
    }
}
