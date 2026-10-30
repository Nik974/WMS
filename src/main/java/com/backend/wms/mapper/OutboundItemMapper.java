package com.backend.wms.mapper;
import com.backend.wms.dto.CreateOutboundItemDto;
import com.backend.wms.dto.OutboundItemDto;
import com.backend.wms.entity.OutboundItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OutboundItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.productName", target = "productName")
    OutboundItemDto toDto(OutboundItem entity);

    List<OutboundItemDto> toDtoList(List<OutboundItem> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    OutboundItem toEntity(CreateOutboundItemDto dto);
}
