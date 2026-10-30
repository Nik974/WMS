package com.backend.wms.mapper;
import com.backend.wms.dto.CreateOutboundOrderDto;
import com.backend.wms.dto.OutboundOrderDto;
import com.backend.wms.entity.OutboundOrder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OutboundItemMapper.class})
public interface OutboundOrderMapper {

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.customerName", target = "customerName")
    OutboundOrderDto toDto(OutboundOrder entity);

    List<OutboundOrderDto> toDtoList(List<OutboundOrder> all);
}