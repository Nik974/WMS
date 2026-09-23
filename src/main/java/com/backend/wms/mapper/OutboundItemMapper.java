package com.backend.wms.mapper;
import com.backend.wms.dto.OutboundItemDto;
import com.backend.wms.entity.OutboundItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OutboundItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "order.id", target = "orderId")
    OutboundItemDto toDto(OutboundItem entity);

    List<OutboundItemDto> toDtoList(List<OutboundItem> entities);

}
