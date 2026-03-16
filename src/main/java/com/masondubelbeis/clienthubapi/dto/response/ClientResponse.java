package com.masondubelbeis.clienthubapi.dto.response;

import java.time.Instant;
import java.util.UUID;

public class ClientResponse {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Instant createdAt;

    public ClientResponse(UUID id, String name, String email, String phone, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}