package com.backend.wms.repository;

import com.backend.wms.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByCustomerNameIgnoreCase(String name);
}
