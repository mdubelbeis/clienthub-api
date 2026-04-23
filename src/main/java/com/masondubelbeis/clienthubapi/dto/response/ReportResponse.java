package com.masondubelbeis.clienthubapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ReportResponse(
        String title,
        LocalDateTime generatedAt,
        List<String> columns,
        List<Map<String, Object>> rows

) {

}
