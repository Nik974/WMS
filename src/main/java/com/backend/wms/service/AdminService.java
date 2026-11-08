package com.backend.wms.service;

import com.backend.wms.dto.UpdateUserDto;
import com.backend.wms.dto.UserDto;
import com.backend.wms.entity.User;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.UserMapper;
import com.backend.wms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Set<String> ROLES = Set.of("WORKER", "MANAGER", "ADMIN");

    public List<UserDto> getAllUsers(){
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Transactional
    public UserDto editUser(Long id, UpdateUserDto dto, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String newUsername = dto.username().trim();
        String newEmail = dto.email().trim().toLowerCase();
        String newRole = dto.role().trim().toUpperCase();

        if (!ROLES.contains(newRole)) {
            throw new IllegalArgumentException("Unknown role: " + dto.role());
        }

        boolean isSelf = user.getId().equals(currentUserId);
        if (isSelf && !user.getRole().equalsIgnoreCase(newRole)) {
            throw new IllegalStateException("You cannot change your own role");
        }
        if (isSelf && !user.getUsername().equals(newUsername)) {
            throw new IllegalStateException("You cannot change your own username");
        }

        if (!user.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new ResourceAlreadyExistsException("User with this email already exists");
        }
        if (!user.getUsername().equalsIgnoreCase(newUsername)
                && userRepository.existsByUsernameIgnoreCase(newUsername)) {
            throw new ResourceAlreadyExistsException("User with this username already exists");
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole()) && !"ADMIN".equals(newRole)
                && userRepository.countByRole("ADMIN") <= 1) {
            throw new IllegalStateException("Cannot demote the last admin");
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setRole(newRole);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto setBlocked(Long id, boolean blocked, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getId().equals(currentUserId)) {
            throw new IllegalStateException("You cannot block yourself");
        }

        if (blocked && user.isEnabled() && "ADMIN".equalsIgnoreCase(user.getRole())
                && userRepository.countByRoleAndEnabledTrue("ADMIN") <= 1) {
            throw new IllegalStateException("Cannot block the last active admin");
        }

        user.setEnabled(!blocked);
        return userMapper.toDto(user);
    }
}