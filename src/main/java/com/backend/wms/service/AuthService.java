package com.backend.wms.service;

import com.backend.wms.dto.AuthResponse;
import com.backend.wms.dto.LoginRequest;
import com.backend.wms.dto.RegisterRequest;
import com.backend.wms.entity.User;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.repository.UserRepository;
import com.backend.wms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResourceAlreadyExistsException("This username is unavailable ");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("This username is unavailable ");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userRepository.save(user);
    }
}
