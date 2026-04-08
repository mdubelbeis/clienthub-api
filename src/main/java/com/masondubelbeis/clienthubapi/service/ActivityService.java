package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ActivityRequest;
import com.masondubelbeis.clienthubapi.dto.request.UpdateActivityStatusRequest;
import com.masondubelbeis.clienthubapi.exception.NotFoundException;
import com.masondubelbeis.clienthubapi.model.Activity;
import com.masondubelbeis.clienthubapi.model.ActivityStatus;
import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.repository.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public Page<Activity> getActivities(Client client, Pageable pageable) {
        return activityRepository.findByClient(client, pageable);
    }

    public Activity createActivity(ActivityRequest request, Client client) {
        Activity activity = new Activity();
        activity.setType(request.type());
        activity.setNotes(request.notes());
        activity.setStatus(ActivityStatus.OPEN);
        activity.setCompletedAt(null);
        activity.setClient(client);

        return activityRepository.save(activity);
    }

    public Activity updateActivity(UUID activityId, ActivityRequest request, Client client) {
        Activity activity = getActivityByIdAndClient(activityId, client);

        activity.setType(request.type());
        activity.setNotes(request.notes());

        return activityRepository.save(activity);
    }

    public Activity updateActivityStatus(UUID activityId, UpdateActivityStatusRequest request, Client client) {
        Activity activity = getActivityByIdAndClient(activityId, client);

        if (request.status() == ActivityStatus.COMPLETED) {
            activity.setStatus(ActivityStatus.COMPLETED);
            activity.setCompletedAt(Instant.now());
        } else if (request.status() == ActivityStatus.OPEN) {
            activity.setStatus(ActivityStatus.OPEN);
            activity.setCompletedAt(null);
        }

        return activityRepository.save(activity);
    }

    public void deleteActivity(UUID activityId, Client client) {
        Activity activity = getActivityByIdAndClient(activityId, client);
        activityRepository.delete(activity);
    }

    private Activity getActivityByIdAndClient(UUID activityId, Client client) {
        return activityRepository.findByIdAndClient(activityId, client)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
    }
}