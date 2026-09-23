package com.backend.wms.mapper;

import com.backend.wms.dto.CustomerDto;
import com.backend.wms.entity.Customer;

import java.util.List;

public interface CustomerMapper{
    CustomerDto toDto(Customer entity);

    List<CustomerDto> toDtoList(List<Customer> entities);
}
