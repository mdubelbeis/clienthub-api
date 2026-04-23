package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ReportRequest;
import com.masondubelbeis.clienthubapi.dto.response.ReportResponse;
import java.util.UUID;

public interface ReportGenerator {
    boolean supports(String reportType);
    ReportResponse generate(UUID userId, ReportRequest request);
}