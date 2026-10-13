package com.backend.wms.mapper;

import com.backend.wms.dto.CustomerDto;
import com.backend.wms.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper{
    @Mapping(target = "customerId", source = "id")
    CustomerDto toDto(Customer entity);

    List<CustomerDto> toDtoList(List<Customer> entities);

    @Mapping(target = "id", source = "customerId")
    Customer toEntity(CustomerDto dto);

    @Mapping(target = "id", ignore = true)
    Customer updateCustomerFromDto(CustomerDto dto, @MappingTarget Customer entity);
}
