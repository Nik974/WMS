package com.backend.wms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id", nullable = false)
    private Long id;

    @Column(name = "supplier_name", nullable = false, length = Integer.MAX_VALUE)
    private String supplierName;

    @Column(name = "tax_id", nullable = false)
    private String taxId;
}