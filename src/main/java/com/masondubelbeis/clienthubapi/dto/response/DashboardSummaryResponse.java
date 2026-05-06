package com.masondubelbeis.clienthubapi.dto.response;

import java.util.List;

public record DashboardSummaryResponse(
        long totalClients,
        long totalActivities,
        long openActivities,
        long completedActivities,
        List<RecentActivityResponse> recentActivities
) {
}