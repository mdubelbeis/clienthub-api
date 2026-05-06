package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.response.DashboardSummaryResponse;
import com.masondubelbeis.clienthubapi.dto.response.RecentActivityResponse;
import com.masondubelbeis.clienthubapi.model.Activity;
import com.masondubelbeis.clienthubapi.model.ActivityStatus;
import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.ActivityRepository;
import com.masondubelbeis.clienthubapi.repository.ClientRepository;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ActivityRepository activityRepository;

    public DashboardService(UserRepository userRepository,
                            ClientRepository clientRepository,
                            ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        long totalClients = clientRepository.countByUser(user);
        long totalActivities = activityRepository.countByClientUser(user);
        long openActivities = activityRepository.countByClientUserAndStatus(user, ActivityStatus.OPEN);
        long completedActivities = activityRepository.countByClientUserAndStatus(user, ActivityStatus.COMPLETED);

        List<RecentActivityResponse> recentActivities = activityRepository
                .findTop5ByClientUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toRecentActivityResponse)
                .toList();

        return new DashboardSummaryResponse(
                totalClients,
                totalActivities,
                openActivities,
                completedActivities,
                recentActivities
        );
    }

    private RecentActivityResponse toRecentActivityResponse(Activity activity) {
        return new RecentActivityResponse(
                activity.getId(),
                activity.getClient().getId(),
                activity.getClient().getName(),
                activity.getType(),
                activity.getStatus(),
                activity.getNotes(),
                activity.getCreatedAt()
        );
    }
}