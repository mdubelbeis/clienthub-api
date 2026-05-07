package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.response.DashboardSummaryResponse;
import com.masondubelbeis.clienthubapi.dto.response.RecentActivityResponse;
import com.masondubelbeis.clienthubapi.model.Activity;
import com.masondubelbeis.clienthubapi.model.ActivityStatus;
import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.ActivityRepository;
import com.masondubelbeis.clienthubapi.repository.ClientRepository;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ActivityRepository activityRepository;

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

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

        log.info("Dashboard summary requested for user {}", userEmail);

        long totalClients = clientRepository.countByUser(user);
        long totalActivities = activityRepository.countByClientUser(user);
        long openActivities = activityRepository.countByClientUserAndStatus(user, ActivityStatus.OPEN);
        long completedActivities = activityRepository.countByClientUserAndStatus(user, ActivityStatus.COMPLETED);

        List<RecentActivityResponse> recentActivities = activityRepository
                .findTop5ByClientUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toRecentActivityResponse)
                .toList();

        log.info(
                "Dashboard summary generated, user={}, totalClients={}, totalActivities={}, openActivities={}, completedActivities={}",
                userEmail,
                totalClients,
                totalActivities,
                openActivities,
                completedActivities
        );

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