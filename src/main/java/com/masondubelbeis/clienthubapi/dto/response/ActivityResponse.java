package com.masondubelbeis.clienthubapi.dto.response;

import com.masondubelbeis.clienthubapi.model.ActivityType;

import java.time.Instant;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        ActivityType type,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}