package com.masondubelbeis.clienthubapi.controller;

import com.masondubelbeis.clienthubapi.dto.request.ActivityRequest;
import com.masondubelbeis.clienthubapi.dto.response.ActivityResponse;
import com.masondubelbeis.clienthubapi.dto.response.ClientResponse;
import com.masondubelbeis.clienthubapi.model.Activity;
import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.service.ActivityService;
import com.masondubelbeis.clienthubapi.service.ClientService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clients/{clientId}/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final ClientService clientService;

    public ActivityController(ActivityService activityService, ClientService clientService) {
        this.activityService = activityService;
        this.clientService = clientService;
    }

    @GetMapping
    public Page<ActivityResponse> getActivities(
            @PathVariable UUID clientId,
            Pageable pageable
    ) {

        Client client = clientService.getClient(clientId);

        return activityService.getActivities(client, pageable)
                .map(activity -> new ActivityResponse(
                        activity.getId(),
                        activity.getType(),
                        activity.getNotes(),
                        activity.getCreatedAt()
                ));
    }

    @PostMapping
    public ActivityResponse createActivity(
            @PathVariable UUID clientId,
            @Valid @RequestBody ActivityRequest request
    ) {

        Client client = clientService.getClient(clientId);

        Activity activity = new Activity();
        activity.setType(request.getType());
        activity.setNotes(request.getNotes());
        activity.setClient(client);

        Activity saved = activityService.createActivity(activity);

        return new ActivityResponse(
                saved.getId(),
                saved.getType(),
                saved.getNotes(),
                saved.getCreatedAt()
        );
    }
}