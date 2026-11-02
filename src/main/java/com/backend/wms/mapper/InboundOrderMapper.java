package com.backend.wms.mapper;
import com.backend.wms.dto.CreateInboundOrderDto;
import com.backend.wms.dto.InboundOrderDto;
import com.backend.wms.entity.InboundOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InboundItemMapper.class})
public interface InboundOrderMapper {

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.supplierName", target = "supplierName")
    InboundOrderDto toDto(InboundOrder entity);

    List<InboundOrderDto> toDtoList(List<InboundOrder> all);
}
