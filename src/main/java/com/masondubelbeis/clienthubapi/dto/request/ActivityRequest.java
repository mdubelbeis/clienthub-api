package com.masondubelbeis.clienthubapi.dto.request;

import com.masondubelbeis.clienthubapi.model.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActivityRequest(

        @NotNull(message = "Activity type is required")
        ActivityType type,

        @NotBlank(message = "Notes are required")
        String notes

) {}