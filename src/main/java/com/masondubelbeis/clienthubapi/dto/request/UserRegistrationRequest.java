package com.masondubelbeis.clienthubapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
    String firstName,

    @NotBlank(message = "Last name is required.")
    String lastName,

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    String email,

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 chars")
    String password
) {}
