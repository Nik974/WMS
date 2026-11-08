package com.backend.wms.controller;

import com.backend.wms.dto.UpdateUserDto;
import com.backend.wms.dto.UserDto;
import com.backend.wms.entity.User;
import com.backend.wms.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> editUser(@PathVariable Long id,
                                            @Valid @RequestBody UpdateUserDto dto,
                                            @AuthenticationPrincipal User current) {
        return ResponseEntity.ok(adminService.editUser(id, dto, current.getId()));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<UserDto> block(@PathVariable Long id,
                                         @AuthenticationPrincipal User current) {
        return ResponseEntity.ok(adminService.setBlocked(id, true, current.getId()));
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<UserDto> unblock(@PathVariable Long id,
                                           @AuthenticationPrincipal User current) {
        return ResponseEntity.ok(adminService.setBlocked(id, false, current.getId()));
    }
}