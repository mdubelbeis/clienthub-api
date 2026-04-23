package com.masondubelbeis.clienthubapi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank(message = "Report type is required")
        String reportType,
        String searchTerm
) {
}