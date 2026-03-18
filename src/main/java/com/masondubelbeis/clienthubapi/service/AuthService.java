package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.LoginRequest;
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
        System.out.println(passwordEncoder.encode("admin"));

    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new NotFoundException("Invalid credentials")
                );

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new NotFoundException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}