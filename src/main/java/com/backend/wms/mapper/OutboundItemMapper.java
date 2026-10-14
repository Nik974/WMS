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
    OutboundItemDto toDto(OutboundItem entity);

    List<OutboundItemDto> toDtoList(List<OutboundItem> entities);

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "order.id", source = "orderId")
    OutboundItem toEntity(CreateOutboundItemDto dto);
}
