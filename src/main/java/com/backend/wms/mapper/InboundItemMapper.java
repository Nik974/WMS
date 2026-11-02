package com.backend.wms.mapper;

import com.backend.wms.dto.CreateInboundItemDto;
import com.backend.wms.dto.InboundItemDto;
import com.backend.wms.entity.InboundItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InboundItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "order.id", target = "orderId")
    InboundItemDto toDto(InboundItem entity);

    List<InboundItemDto> toDtoList(List<InboundItem> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    InboundItem toEntity(CreateInboundItemDto dto);
}

