package com.backend.wms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "http_method", nullable = false)
    private String httpMethod;

    @Column(nullable = false)
    private String resource;

    @Column(name = "resource_id")
    private Long resourceId;

    private String action;
    private String query;

    @Column(nullable = false)
    private String path;
}
