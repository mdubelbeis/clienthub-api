package com.masondubelbeis.clienthubapi.dto.request;

import com.masondubelbeis.clienthubapi.model.ActivityType;
import jakarta.validation.constraints.NotNull;

public class ActivityRequest {

    @NotNull
    private ActivityType type;

    private String notes;

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}