package com.masondubelbeis.clienthubapi.dto.response;

import com.masondubelbeis.clienthubapi.model.ActivityType;

public record ActivityTypeCountResponse(
        ActivityType type,
        long count
) {
}