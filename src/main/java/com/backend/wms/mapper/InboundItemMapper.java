package com.backend.wms.mapper;

import com.backend.wms.dto.InboundItemDto;
import com.backend.wms.entity.InboundItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InboundItemMapper {

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    InboundItemDto toDto(InboundItem entity);

    List<InboundItemDto> toDtoList(List<InboundItem> entities);
}

