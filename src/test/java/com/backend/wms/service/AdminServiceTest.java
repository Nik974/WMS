package com.backend.wms.service;

import com.backend.wms.dto.UpdateUserDto;
import com.backend.wms.entity.User;
import com.backend.wms.mapper.UserMapper;
import com.backend.wms.repository.UserRepository;
import com.backend.wms.dto.UserDto;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private static final Long ME = 1L;

    private User user(Long id, String username, String email, String role) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setEmail(email);
        u.setRole(role);
        u.setEnabled(true);
        return u;
    }

    private UserDto dto(Long id, String username, String email, String role, boolean enabled) {
        return new UserDto(id, username, email, role, LocalDateTime.now(), enabled);
    }

    @Test
    void getAllUsers_ShouldReturnMappedList() {
        List<User> users = List.of(user(1L, "a", "a@x.com", "ADMIN"));
        List<UserDto> dtos = List.of(dto(1L, "a", "a@x.com", "ADMIN", true));

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDtoList(users)).thenReturn(dtos);

        assertEquals(dtos, adminService.getAllUsers());
    }

    @Test
    void editUser_ShouldThrowNotFound_WhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adminService.editUser(99L, new UpdateUserDto("bob", "b@x.com", "WORKER"), ME));
        verifyNoInteractions(userMapper);
    }

    @Test
    void editUser_ShouldThrow_WhenRoleUnknown() {
        User bob = user(2L, "bob", "bob@x.com", "WORKER");
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));

        assertThrows(IllegalArgumentException.class,
                () -> adminService.editUser(2L, new UpdateUserDto("bob", "bob@x.com", "SUPERUSER"), ME));
        assertEquals("WORKER", bob.getRole());
    }

    @Test
    void editUser_ShouldThrow_WhenAdminChangesOwnRole() {
        User me = user(ME, "boss", "boss@x.com", "ADMIN");
        when(userRepository.findById(ME)).thenReturn(Optional.of(me));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminService.editUser(ME, new UpdateUserDto("boss", "boss@x.com", "MANAGER"), ME));

        assertEquals("You cannot change your own role", ex.getMessage());
        assertEquals("ADMIN", me.getRole());
    }

    @Test
    void editUser_ShouldThrow_WhenAdminChangesOwnUsername() {
        User me = user(ME, "boss", "boss@x.com", "ADMIN");
        when(userRepository.findById(ME)).thenReturn(Optional.of(me));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminService.editUser(ME, new UpdateUserDto("newboss", "boss@x.com", "ADMIN"), ME));

        assertEquals("You cannot change your own username", ex.getMessage());
        assertEquals("boss", me.getUsername());
    }

    @Test
    void editUser_ShouldAllowSelf_WhenRoleAndUsernameUnchanged() {
        User me = user(ME, "boss", "boss@x.com", "ADMIN");
        UserDto expected = dto(ME, "boss", "new@x.com", "ADMIN", true);

        when(userRepository.findById(ME)).thenReturn(Optional.of(me));
        when(userMapper.toDto(me)).thenReturn(expected);

        // роль передана в другом регистре: это не считается сменой роли
        UserDto result = adminService.editUser(ME, new UpdateUserDto("boss", "new@x.com", "admin"), ME);

        assertEquals(expected, result);
        assertEquals("new@x.com", me.getEmail());
        assertEquals("ADMIN", me.getRole());
    }

    @Test
    void editUser_ShouldThrow_WhenEmailAlreadyUsed() {
        User bob = user(2L, "bob", "bob@x.com", "WORKER");
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(userRepository.existsByEmailIgnoreCase("taken@x.com")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> adminService.editUser(2L, new UpdateUserDto("bob", "taken@x.com", "WORKER"), ME));
        assertEquals("bob@x.com", bob.getEmail());
    }

    @Test
    void editUser_ShouldThrow_WhenUsernameAlreadyUsed() {
        User bob = user(2L, "bob", "bob@x.com", "WORKER");
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(userRepository.existsByUsernameIgnoreCase("alice")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> adminService.editUser(2L, new UpdateUserDto("alice", "bob@x.com", "WORKER"), ME));
        assertEquals("bob", bob.getUsername());
    }

    @Test
    void editUser_ShouldNotCheckDuplicates_WhenEmailAndUsernameOnlyChangeCase() {
        User bob = user(2L, "bob", "bob@x.com", "WORKER");
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));

        adminService.editUser(2L, new UpdateUserDto("Bob", "BOB@x.com", "WORKER"), ME);

        verify(userRepository, never()).existsByEmailIgnoreCase(any());
        verify(userRepository, never()).existsByUsernameIgnoreCase(any());
    }

    @Test
    void editUser_ShouldThrow_WhenDemotingLastAdmin() {
        User otherAdmin = user(5L, "other", "o@x.com", "ADMIN");
        when(userRepository.findById(5L)).thenReturn(Optional.of(otherAdmin));
        when(userRepository.countByRole("ADMIN")).thenReturn(1L);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminService.editUser(5L, new UpdateUserDto("other", "o@x.com", "MANAGER"), ME));

        assertEquals("Cannot demote the last admin", ex.getMessage());
        assertEquals("ADMIN", otherAdmin.getRole());
    }

    @Test
    void editUser_ShouldDemoteAdmin_WhenAnotherAdminExists() {
        User otherAdmin = user(5L, "other", "o@x.com", "ADMIN");
        when(userRepository.findById(5L)).thenReturn(Optional.of(otherAdmin));
        when(userRepository.countByRole("ADMIN")).thenReturn(2L);

        adminService.editUser(5L, new UpdateUserDto("other", "o@x.com", "MANAGER"), ME);

        assertEquals("MANAGER", otherAdmin.getRole());
    }

    @Test
    void editUser_ShouldNormalizeAndUpdateFields_WhenValid() {
        User bob = user(2L, "bob", "bob@x.com", "WORKER");
        UserDto expected = dto(2L, "robert", "robert@x.com", "MANAGER", true);

        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(userMapper.toDto(bob)).thenReturn(expected);

        UserDto result = adminService.editUser(2L,
                new UpdateUserDto("  robert ", " Robert@X.com ", " manager "), ME);

        assertEquals(expected, result);
        assertEquals("robert", bob.getUsername());
        assertEquals("robert@x.com", bob.getEmail());
        assertEquals("MANAGER", bob.getRole());
        verify(userRepository).existsByEmailIgnoreCase("robert@x.com");
        verify(userRepository).existsByUsernameIgnoreCase("robert");
    }

    @Test
    void setBlocked_ShouldThrowNotFound_WhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.setBlocked(99L, true, ME));
    }

    @Test
    void setBlocked_ShouldThrow_WhenBlockingSelf() {
        User me = user(ME, "boss", "boss@x.com", "ADMIN");
        when(userRepository.findById(ME)).thenReturn(Optional.of(me));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminService.setBlocked(ME, true, ME));

        assertEquals("You cannot block yourself", ex.getMessage());
        assertTrue(me.isEnabled());
    }

    @Test
    void setBlocked_ShouldThrow_WhenBlockingLastActiveAdmin() {
        User admin = user(2L, "admin2", "a2@x.com", "ADMIN");
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.countByRoleAndEnabledTrue("ADMIN")).thenReturn(1L);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminService.setBlocked(2L, true, ME));

        assertEquals("Cannot block the last active admin", ex.getMessage());
        assertTrue(admin.isEnabled());
    }

    @Test
    void setBlocked_ShouldBlockAdmin_WhenOtherActiveAdminsExist() {
        User admin = user(2L, "admin2", "a2@x.com", "ADMIN");
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.countByRoleAndEnabledTrue("ADMIN")).thenReturn(2L);

        adminService.setBlocked(2L, true, ME);

        assertFalse(admin.isEnabled());
    }

    @Test
    void setBlocked_ShouldBlockWorker_WithoutAdminCountCheck() {
        User worker = user(3L, "w", "w@x.com", "WORKER");
        UserDto expected = dto(3L, "w", "w@x.com", "WORKER", false);

        when(userRepository.findById(3L)).thenReturn(Optional.of(worker));
        when(userMapper.toDto(worker)).thenReturn(expected);

        UserDto result = adminService.setBlocked(3L, true, ME);

        assertEquals(expected, result);
        assertFalse(worker.isEnabled());
        verify(userRepository, never()).countByRoleAndEnabledTrue(any());
    }

    @Test
    void setBlocked_ShouldUnblockUser() {
        User worker = user(3L, "w", "w@x.com", "WORKER");
        worker.setEnabled(false);
        when(userRepository.findById(3L)).thenReturn(Optional.of(worker));

        adminService.setBlocked(3L, false, ME);

        assertTrue(worker.isEnabled());
    }

    @Test
    void setBlocked_ShouldBeIdempotent_WhenAdminAlreadyBlocked() {
        User admin = user(2L, "admin2", "a2@x.com", "ADMIN");
        admin.setEnabled(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        adminService.setBlocked(2L, true, ME);

        assertFalse(admin.isEnabled());
        verify(userRepository, never()).countByRoleAndEnabledTrue(any());
    }
}