package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.response.ReportResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public abstract class AbstractReportGenerator implements ReportGenerator {
    protected ReportResponse buildResponse(
            String title,
            List<String> columns,
            List<Map<String, Object>> rows

    ) {
        return new ReportResponse(
                title,
                LocalDateTime.now(),
                columns,
                rows
        );
    }

    protected String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

}