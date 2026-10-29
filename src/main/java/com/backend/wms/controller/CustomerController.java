package com.backend.wms.controller;

import java.util.List;

import com.backend.wms.dto.CustomerDto;
import com.backend.wms.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping    
    public ResponseEntity<List<CustomerDto>> getAllCustomers(){
       List<CustomerDto> list = customerService.getAllCustomers();
       if(list.isEmpty()) return ResponseEntity.status(204).body(null);
       return ResponseEntity.ok(list);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomerDto> createNewCustomer(@Valid @RequestBody CustomerDto dto){
        return ResponseEntity.status(201).body(customerService.addCustomer(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomerDto> editCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDto dto){
        return ResponseEntity.status(200).body(customerService.editCustomer(id, dto));
    }
}
