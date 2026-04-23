package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ReportRequest;
import com.masondubelbeis.clienthubapi.dto.response.ReportResponse;
import com.masondubelbeis.clienthubapi.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final List<ReportGenerator> reportGenerators;

    public ReportResponse generate(UUID userId, ReportRequest request) {
        return reportGenerators.stream()
                .filter(generator -> generator.supports(request.reportType()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Unsupported report type: " + request.reportType()
                ))
                .generate(userId, request);
    }
}