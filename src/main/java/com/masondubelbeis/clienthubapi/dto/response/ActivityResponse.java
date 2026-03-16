package com.masondubelbeis.clienthubapi.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        String type,
        String notes,
        Instant createdAt,
        UUID clientId
) {}