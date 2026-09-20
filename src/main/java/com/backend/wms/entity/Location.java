package com.backend.wms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "zone", nullable = false, length = Integer.MAX_VALUE)
    private String zone;

    @Column(name = "aisle", nullable = false, length = Integer.MAX_VALUE)
    private String aisle;

    @Column(name = "rack", length = Integer.MAX_VALUE)
    private String rack;

    @Column(name = "shelf", nullable = false, length = Integer.MAX_VALUE)
    private String shelf;

    @Column(name = "bin", nullable = false, length = Integer.MAX_VALUE)
    private String bin;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "code", nullable = false, length = Integer.MAX_VALUE)
    private String code;

}