package com.masondubelbeis.clienthubapi.dto.response;

import com.masondubelbeis.clienthubapi.model.ActivityType;

import java.time.Instant;
import java.util.UUID;

public class ActivityResponse {

    private UUID id;
    private ActivityType type;
    private String notes;
    private Instant createdAt;

    public ActivityResponse(UUID id, ActivityType type, String notes, Instant createdAt) {
        this.id = id;
        this.type = type;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public ActivityType getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}