package com.backend.wms.mapper;

import com.backend.wms.dto.CustomerDto;
import com.backend.wms.entity.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper{
    CustomerDto toDto(Customer entity);

    List<CustomerDto> toDtoList(List<Customer> entities);

    Customer toEntity(CustomerDto dto);

    Customer updateCustomerFromDto(CustomerDto dto, Customer entity);
}
