package com.masondubelbeis.clienthubapi.controller;

import com.masondubelbeis.clienthubapi.dto.request.ActivityRequest;
import com.masondubelbeis.clienthubapi.dto.request.UpdateActivityStatusRequest;
import com.masondubelbeis.clienthubapi.dto.response.ActivityResponse;
import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.service.ActivityService;
import com.masondubelbeis.clienthubapi.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/clients/{clientId}/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ClientService clientService;

    @GetMapping
    public Page<ActivityResponse> getActivities(
            @PathVariable UUID clientId,
            Pageable pageable
    ) {
        Client client = clientService.getClientEntity(clientId);

        return activityService.getActivities(client, pageable)
                .map(activity -> new ActivityResponse(
                        activity.getId(),
                        activity.getType(),
                        activity.getNotes(),
                        activity.getStatus(),
                        activity.getCreatedAt(),
                        activity.getUpdatedAt(),
                        activity.getCompletedAt()
                ));
    }

    @PostMapping
    public ActivityResponse createActivity(
            @PathVariable UUID clientId,
            @Valid @RequestBody ActivityRequest request
    ) {
        Client client = clientService.getClientEntity(clientId);
        var saved = activityService.createActivity(request, client);

        return new ActivityResponse(
                saved.getId(),
                saved.getType(),
                saved.getNotes(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getCompletedAt()
        );
    }

    @PutMapping("/{activityId}")
    public ActivityResponse updateActivity(
            @PathVariable UUID clientId,
            @PathVariable UUID activityId,
            @Valid @RequestBody ActivityRequest request
    ) {
        Client client = clientService.getClientEntity(clientId);
        var updated = activityService.updateActivity(activityId, request, client);

        return new ActivityResponse(
                updated.getId(),
                updated.getType(),
                updated.getNotes(),
                updated.getStatus(),
                updated.getCreatedAt(),
                updated.getUpdatedAt(),
                updated.getCompletedAt()
        );
    }

    @PatchMapping("/{activityId}/status")
    public ActivityResponse updateActivityStatus(
            @PathVariable UUID clientId,
            @PathVariable UUID activityId,
            @Valid @RequestBody UpdateActivityStatusRequest request
    ) {
        Client client = clientService.getClientEntity(clientId);
        var updated = activityService.updateActivityStatus(activityId, request, client);

        return new ActivityResponse(
                updated.getId(),
                updated.getType(),
                updated.getNotes(),
                updated.getStatus(),
                updated.getCreatedAt(),
                updated.getUpdatedAt(),
                updated.getCompletedAt()
        );
    }

    @DeleteMapping("/{activityId}")
    public void deleteActivity(
            @PathVariable UUID clientId,
            @PathVariable UUID activityId
    ) {
        Client client = clientService.getClientEntity(clientId);
        activityService.deleteActivity(activityId, client);
    }
}