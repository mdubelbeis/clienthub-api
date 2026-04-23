package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ReportRequest;
import com.masondubelbeis.clienthubapi.dto.response.ReportResponse;
import com.masondubelbeis.clienthubapi.model.Activity;
import com.masondubelbeis.clienthubapi.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ActivityReportGenerator extends AbstractReportGenerator {
    private final ActivityRepository activityRepository;
    @Override
    public boolean supports(String reportType) {
        return "activities".equalsIgnoreCase(reportType);
    }
    @Override
    public ReportResponse generate(UUID userId, ReportRequest request) {
        String search = normalize(request.searchTerm());
        List<Activity> activities = activityRepository.findByClientUserId(userId)
                .stream()
                .filter(activity -> {
                    if (search.isBlank()) {
                        return true;
                    }

                    boolean matchesType = activity.getType() != null
                            && activity.getType().name().toLowerCase().contains(search);

                    boolean matchesStatus = activity.getStatus() != null
                            && activity.getStatus().name().toLowerCase().contains(search);

                    boolean matchesNotes = activity.getNotes() != null
                            && activity.getNotes().toLowerCase().contains(search);

                    boolean matchesClientName = activity.getClient() != null
                            && activity.getClient().getName() != null
                            && activity.getClient().getName().toLowerCase().contains(search);

                    return matchesType || matchesStatus || matchesNotes || matchesClientName;
                })
                .toList();

        List<Map<String, Object>> rows = activities.stream()
                .map(activity -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("clientName", activity.getClient().getName());
                    row.put("type", activity.getType());
                    row.put("status", activity.getStatus());
                    row.put("notes", activity.getNotes());
                    row.put("createdAt", activity.getCreatedAt());
                    row.put("updatedAt", activity.getUpdatedAt());
                    row.put("completedAt", activity.getCompletedAt());

                    return row;

                })
                .toList();

        return buildResponse(
                "Activity Report",
                List.of("clientName", "type", "status", "notes", "createdAt", "updatedAt", "completedAt"),
                rows
        );
    }
}