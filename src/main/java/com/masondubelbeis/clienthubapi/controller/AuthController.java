package com.masondubelbeis.clienthubapi.controller;

import com.masondubelbeis.clienthubapi.dto.request.LoginRequest;
import com.masondubelbeis.clienthubapi.dto.request.UserRegistrationRequest;
import com.masondubelbeis.clienthubapi.dto.response.AuthResponse;
import com.masondubelbeis.clienthubapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserRegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

}
