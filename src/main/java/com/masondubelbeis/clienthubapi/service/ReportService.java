package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ReportRequest;
import com.masondubelbeis.clienthubapi.dto.response.ReportResponse;
import com.masondubelbeis.clienthubapi.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final List<ReportGenerator> reportGenerators;

    public ReportResponse generate(UUID userId, ReportRequest request) {
        log.info(
                "Report generation requested. userId={}, reportType={}, hasSearchTerm={}",
                userId,
                request.reportType(),
                request.searchTerm() != null && !request.searchTerm().isBlank()
        );

        ReportGenerator reportGenerator = reportGenerators.stream()
                .filter(generator -> generator.supports(request.reportType()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn(
                            "Report generation rejected: unsupported report type. userId={}, reportType={}",
                            userId,
                            request.reportType()
                    );
                    return new BadRequestException(
                            "Unsupported report type: " + request.reportType()
                    );
                });

        ReportResponse response = reportGenerator.generate(userId, request);

        log.info(
                "Report generated successfully. userId={}, reportType={}, rowCount={}",
                userId,
                request.reportType(),
                response.rows() == null ? 0 : response.rows().size()
        );

        return response;
    }
}