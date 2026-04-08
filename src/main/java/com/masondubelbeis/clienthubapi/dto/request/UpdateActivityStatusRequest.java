package com.masondubelbeis.clienthubapi.dto.request;

import com.masondubelbeis.clienthubapi.model.ActivityStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateActivityStatusRequest(
        @NotNull(message = "Status is required")
        ActivityStatus status
) {}