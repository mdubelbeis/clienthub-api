package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ActivityRequest;
import com.masondubelbeis.clienthubapi.model.Activity;
import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.repository.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        activity.setClient(client);

        return activityRepository.save(activity);
    }
}