package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ReportRequest;
import com.masondubelbeis.clienthubapi.dto.response.ReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private ReportGenerator clientReportGenerator;
    private ReportGenerator activityReportGenerator;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        clientReportGenerator = Mockito.mock(ReportGenerator.class);
        activityReportGenerator = Mockito.mock(ReportGenerator.class);

        reportService = new ReportService(List.of(clientReportGenerator, activityReportGenerator));
    }

    @Test
    @DisplayName("generate should return activity report when activities report type is requested")
    void generate_shouldReturnActivityReport_whenActivitiesReportTypeRequested() {
        UUID userId = UUID.randomUUID();
        ReportRequest request = new ReportRequest("activities", "open");

        ReportResponse expectedResponse = new ReportResponse(
                "Activity Report",
                LocalDateTime.now(),
                List.of("clientName", "type", "status", "notes"),
                List.of(
                        Map.of(
                                "clientName", "John Smith",
                                "type", "CALL",
                                "status", "OPEN",
                                "notes", "Follow-up scheduled"
                        )
                )
        );

        when(clientReportGenerator.supports("activities")).thenReturn(false);
        when(activityReportGenerator.supports("activities")).thenReturn(true);
        when(activityReportGenerator.generate(userId, request)).thenReturn(expectedResponse);

        ReportResponse actualResponse = reportService.generate(userId, request);

        assertEquals(expectedResponse, actualResponse);
        verify(clientReportGenerator).supports("activities");
        verify(activityReportGenerator).supports("activities");
        verify(activityReportGenerator).generate(userId, request);
        verifyNoMoreInteractions(clientReportGenerator, activityReportGenerator);
    }
}