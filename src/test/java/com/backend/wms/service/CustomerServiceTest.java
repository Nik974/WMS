package com.backend.wms.service;
import com.backend.wms.dto.CustomerDto;
import com.backend.wms.entity.Customer;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.CustomerMapper;
import com.backend.wms.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;


    @Test
    void addCustomer_ShouldSaveAndReturnDto_WhenNameIsUnique() {
        CustomerDto inputDto = new CustomerDto(null, "TestName Corp");

        Customer entity = new Customer();
        entity.setCustomerName("TestName Corp");

        Customer savedEntity = new Customer();
        savedEntity.setId(1L);
        savedEntity.setCustomerName("TestName Corp");

        CustomerDto expectedDto = new CustomerDto(1L, "TestName Corp");

        when(customerRepository.existsByCustomerNameIgnoreCase("TestName Corp")).thenReturn(false);
        when(customerMapper.toEntity(inputDto)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(savedEntity);
        when(customerMapper.toDto(savedEntity)).thenReturn(expectedDto);

        CustomerDto result = customerService.addCustomer(inputDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(customerRepository).existsByCustomerNameIgnoreCase("TestName Corp");
        verify(customerRepository).save(entity);
    }

    @Test
    void addCustomer_ShouldThrowException_WhenNameAlreadyExists() {
        CustomerDto inputDto = new CustomerDto(null, "TestName Corp");

        when(customerRepository.existsByCustomerNameIgnoreCase("TestName Corp")).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> customerService.addCustomer(inputDto)
        );

        assertEquals("Customer already exists", exception.getMessage());
        verify(customerRepository).existsByCustomerNameIgnoreCase("TestName Corp");
        verify(customerRepository, never()).save(any());
        verifyNoInteractions(customerMapper);
    }


    @Test
    void editCustomer_ShouldUpdateAndReturnDto_WhenCustomerExistsAndNameIsUnique() {
        Long customerId = 1L;
        CustomerDto updateDto = new CustomerDto(customerId, "New Name");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setCustomerName("Old Name");

        CustomerDto expectedDto = new CustomerDto(customerId, "New Name");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByCustomerNameIgnoreCase("New Name")).thenReturn(false);

        doAnswer(invocation -> {
            existingCustomer.setCustomerName(updateDto.customerName());
            return null;
        }).when(customerMapper).updateCustomerFromDto(updateDto, existingCustomer);

        when(customerMapper.toDto(existingCustomer)).thenReturn(expectedDto);

        CustomerDto result = customerService.editCustomer(customerId, updateDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals("New Name", existingCustomer.getCustomerName());

        verify(customerRepository).findById(customerId);
        verify(customerRepository).existsByCustomerNameIgnoreCase("New Name");
        verify(customerMapper).updateCustomerFromDto(updateDto, existingCustomer);
    }

    @Test
    void editCustomer_ShouldThrowNotFoundException_WhenCustomerDoesNotExist() {
        Long nonExistentId = 99L;
        CustomerDto updateDto = new CustomerDto(nonExistentId, "Name");

        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.editCustomer(nonExistentId, updateDto)
        );

        assertEquals("Customer does not exist", exception.getMessage());
        verify(customerRepository).findById(nonExistentId);
        verifyNoMoreInteractions(customerRepository);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void editCustomer_ShouldThrowAlreadyExistsException_WhenNewNameIsTaken() {
        Long customerId = 1L;
        CustomerDto updateDto = new CustomerDto(customerId, "Taken Name");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setCustomerName("Old Name");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByCustomerNameIgnoreCase("Taken Name")).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> customerService.editCustomer(customerId, updateDto)
        );

        assertEquals("Customer already exists", exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(customerRepository).existsByCustomerNameIgnoreCase("Taken Name");
        verify(customerMapper, never()).updateCustomerFromDto(any(), any());
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() {
        Customer customer = new Customer();
        customer.setId(1L);
        List<Customer> customers = List.of(customer);

        CustomerDto dto = new CustomerDto(1L, "TestName");
        List<CustomerDto> expectedDtos = List.of(dto);

        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toDtoList(customers)).thenReturn(expectedDtos);

        List<CustomerDto> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);

        verify(customerRepository).findAll();
        verify(customerMapper).toDtoList(customers);
    }

    @Test
    void existsByCustomersNameIgnoreCase_ShouldDelegateToRepository() {
        String name = "Test";
        when(customerRepository.existsByCustomerNameIgnoreCase(name)).thenReturn(true);

        boolean result = customerService.existsByCustomersNameIgnoreCase(name);

        assertTrue(result);
        verify(customerRepository).existsByCustomerNameIgnoreCase(name);
    }
}