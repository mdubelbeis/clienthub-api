package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.LoginRequest;
import com.masondubelbeis.clienthubapi.dto.request.UserRegistrationRequest;
import com.masondubelbeis.clienthubapi.dto.response.AuthResponse;
import com.masondubelbeis.clienthubapi.exception.BadRequestException;
import com.masondubelbeis.clienthubapi.exception.NotFoundException;
import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import com.masondubelbeis.clienthubapi.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found. email={}", normalizedEmail);
                    return new NotFoundException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: invalid password. userId={}, email={}", user.getId(), user.getEmail());
            throw new NotFoundException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        log.info("User login successful. userId={}, email={}", user.getId(), user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse register(UserRegistrationRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Registration failed: email already in use. email={}", normalizedEmail);
            throw new BadRequestException("Email already in use");
        }

        User user = new User();
        user.setFirstName(normalizeName(request.firstName()));
        user.setLastName(normalizeName(request.lastName()));
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        log.info("User registered successfully. userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }
}