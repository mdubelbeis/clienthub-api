package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ActivityRequest;
import com.masondubelbeis.clienthubapi.dto.request.UpdateActivityStatusRequest;
import com.masondubelbeis.clienthubapi.exception.NotFoundException;
import com.masondubelbeis.clienthubapi.model.Activity;
import com.masondubelbeis.clienthubapi.model.ActivityStatus;
import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.repository.ActivityRepository;
import com.masondubelbeis.clienthubapi.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ActivityService {

    private static final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public Page<Activity> getActivities(Client client, Pageable pageable) {
        Page<Activity> activities = activityRepository.findByClient(client, pageable);

        log.debug(
                "Activity list retrieved. clientId={}, page={}, size={}, totalElements={}",
                client.getId(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                activities.getTotalElements()
        );

        return activities;
    }

    @Transactional
    public Activity createActivity(ActivityRequest request, Client client) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();

        Activity activity = new Activity();
        activity.setType(request.type());
        activity.setNotes(request.notes());
        activity.setStatus(ActivityStatus.OPEN);
        activity.setCompletedAt(null);
        activity.setClient(client);

        Activity savedActivity = activityRepository.save(activity);

        log.info(
                "Activity created. activityId={}, clientId={}, type={}, status={}, currentUserEmail={}",
                savedActivity.getId(),
                client.getId(),
                savedActivity.getType(),
                savedActivity.getStatus(),
                currentUserEmail
        );

        return savedActivity;
    }

    @Transactional
    public Activity updateActivity(UUID activityId, ActivityRequest request, Client client) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();

        Activity activity = getActivityByIdAndClient(activityId, client);

        activity.setType(request.type());
        activity.setNotes(request.notes());

        Activity updatedActivity = activityRepository.save(activity);

        log.info(
                "Activity updated. activityId={}, clientId={}, type={}, status={}, currentUserEmail={}",
                updatedActivity.getId(),
                client.getId(),
                updatedActivity.getType(),
                updatedActivity.getStatus(),
                currentUserEmail
        );

        return updatedActivity;
    }

    @Transactional
    public Activity updateActivityStatus(UUID activityId, UpdateActivityStatusRequest request, Client client) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();

        Activity activity = getActivityByIdAndClient(activityId, client);
        ActivityStatus previousStatus = activity.getStatus();

        if (request.status() == ActivityStatus.COMPLETED) {
            activity.setStatus(ActivityStatus.COMPLETED);
            activity.setCompletedAt(Instant.now());
        } else if (request.status() == ActivityStatus.OPEN) {
            activity.setStatus(ActivityStatus.OPEN);
            activity.setCompletedAt(null);
        }

        Activity updatedActivity = activityRepository.save(activity);

        log.info(
                "Activity status updated. activityId={}, clientId={}, previousStatus={}, newStatus={}, currentUserEmail={}",
                updatedActivity.getId(),
                client.getId(),
                previousStatus,
                updatedActivity.getStatus(),
                currentUserEmail
        );

        return updatedActivity;
    }

    @Transactional
    public void deleteActivity(UUID activityId, Client client) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();

        Activity activity = getActivityByIdAndClient(activityId, client);

        activityRepository.delete(activity);

        log.info(
                "Activity deleted. activityId={}, clientId={}, currentUserEmail={}",
                activity.getId(),
                client.getId(),
                currentUserEmail
        );
    }

    private Activity getActivityByIdAndClient(UUID activityId, Client client) {
        return activityRepository.findByIdAndClient(activityId, client)
                .orElseThrow(() -> {
                    log.warn(
                            "Activity lookup failed: activity not found for client. activityId={}, clientId={}",
                            activityId,
                            client.getId()
                    );
                    return new NotFoundException("Activity not found");
                });
    }
}