package com.masondubelbeis.clienthubapi.dto.request;

public record UserRegistrationRequest(
    String firstName,
    String lastName,
    String email,
    String password
) {}
