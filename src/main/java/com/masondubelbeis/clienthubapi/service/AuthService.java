package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.LoginRequest;
import com.masondubelbeis.clienthubapi.dto.request.UserRegistrationRequest;
import com.masondubelbeis.clienthubapi.dto.response.AuthResponse;
import com.masondubelbeis.clienthubapi.exception.NotFoundException;
import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import com.masondubelbeis.clienthubapi.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

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
                .orElseThrow(() ->
                        new NotFoundException("No username found: " + request.email())
                );

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new NotFoundException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public AuthResponse register(UserRegistrationRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
                ("Email already in use");
        }

        User user = new User();
        user.setFirstName(normalizeName(request.firstName()));
        user.setLastName(normalizeName(request.lastName()));
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        String  token = jwtService.generateToken(savedUser);
        return new AuthResponse(token);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();

    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }
}