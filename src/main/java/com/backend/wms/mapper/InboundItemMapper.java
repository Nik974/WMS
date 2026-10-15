package com.backend.wms.mapper;

import com.backend.wms.dto.CreateInboundItemDto;
import com.backend.wms.dto.InboundItemDto;
import com.backend.wms.entity.InboundItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InboundItemMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    InboundItemDto toDto(InboundItem entity);

    List<InboundItemDto> toDtoList(List<InboundItem> entities);

    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "product.id", source = "productId")
    InboundItem toEntity(CreateInboundItemDto dto);
}

