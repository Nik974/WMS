package com.backend.wms.repository;

import com.backend.wms.entity.User;
import org.flywaydb.core.internal.util.ClassUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    long countByRole(String admin);

    long countByRoleAndEnabledTrue(String admin);
}
