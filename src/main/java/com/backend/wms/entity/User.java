package com.backend.wms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = Integer.MAX_VALUE)
    private String username;

    @Column(name = "password_hash", length = Integer.MAX_VALUE)
    private String passwordHash;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "role", nullable = false, length = Integer.MAX_VALUE)
    private String role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


}