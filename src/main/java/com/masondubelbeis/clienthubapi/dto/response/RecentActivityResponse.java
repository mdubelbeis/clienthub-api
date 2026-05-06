package com.masondubelbeis.clienthubapi.dto.response;

import com.masondubelbeis.clienthubapi.model.ActivityStatus;
import com.masondubelbeis.clienthubapi.model.ActivityType;

import java.time.Instant;
import java.util.UUID;

public record RecentActivityResponse(
        UUID id,
        UUID clientId,
        String clientName,
        ActivityType type,
        ActivityStatus status,
        String notes,
        Instant createdAt
) {
}