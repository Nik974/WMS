package com.backend.wms.repository;

import com.backend.wms.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
            select a from AuditLog a
            where a.createdAt >= :from and a.createdAt < :to
              and lower(a.username) like lower(concat('%', :user, '%'))
              and (:resource = '' or a.resource = :resource)
            """)
    Page<AuditLog> search(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
                          @Param("user") String user, @Param("resource") String resource,
                          Pageable pageable);
}