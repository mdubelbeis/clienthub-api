package com.masondubelbeis.clienthubapi.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String name,
        String email,
        String phone,
        Instant createdAt
) {}