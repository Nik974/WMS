package com.backend.wms.mapper;
import com.backend.wms.dto.CreateOutboundOrderDto;
import com.backend.wms.dto.OutboundOrderDto;
import com.backend.wms.entity.OutboundOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OutboundItemMapper.class})
public interface OutboundOrderMapper {

    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "customer.customerName", target = "customerName")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "id", target = "orderId")
    OutboundOrderDto toDto(OutboundOrder entity);

    @Mapping(source = "warehouseId", target = "warehouse.id")
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "orderId", target = "id")
    @Mapping(source = "customerName", target = "customer.customerName")
    OutboundOrder toEntity(CreateOutboundOrderDto dto);

    List<OutboundOrderDto> toDtoList(List<OutboundOrder> all);
}
