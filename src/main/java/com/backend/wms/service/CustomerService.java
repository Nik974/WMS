package com.backend.wms.service;


import com.backend.wms.dto.CustomerDto;
import com.backend.wms.entity.Customer;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.CustomerMapper;
import com.backend.wms.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerDto addCustomer(CustomerDto dto){
        if (customerRepository.existsByCustomerNameIgnoreCase(dto.customerName())){
            throw new ResourceAlreadyExistsException("Customer already exists");
        }
        Customer entity = customerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(entity);
        return customerMapper.toDto(savedCustomer);
    }

    @Transactional
    public CustomerDto editCustomer(Long id, CustomerDto dto){

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer does not exist"));

        boolean isCustomerNameChanged = customer.getCustomerName().equalsIgnoreCase(dto.customerName());

        if (isCustomerNameChanged && customerRepository.existsByCustomerNameIgnoreCase(dto.customerName())){
            throw new ResourceAlreadyExistsException("Customer already exists");
        }

        customerMapper.updateCustomerFromDto(dto,customer);
        return customerMapper.toDto(customer);
    }

    public List<CustomerDto> getAllCustomers(){
        return customerMapper.toDtoList(customerRepository.findAll());
    }

    public boolean existsByCustomersNameIgnoreCase(String customerName){
        return customerRepository.existsByCustomerNameIgnoreCase(customerName);
    }
}
