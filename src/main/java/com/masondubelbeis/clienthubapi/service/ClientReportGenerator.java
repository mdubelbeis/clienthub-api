package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ReportRequest;
import com.masondubelbeis.clienthubapi.dto.response.ReportResponse;
import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.ClientRepository;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientReportGenerator extends AbstractReportGenerator {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Override
    public boolean supports(String reportType) {
        return "clients".equalsIgnoreCase(reportType);
    }

    @Override
    public ReportResponse generate(UUID userId, ReportRequest request) {
        String search = normalize(request.searchTerm());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Client> clients = clientRepository.findByUser(user, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(client -> {
                    if (search.isBlank()) {
                        return true;
                    }

                    boolean matchesName = client.getName() != null
                            && client.getName().toLowerCase().contains(search);
                    boolean matchesEmail = client.getEmail() != null
                            && client.getEmail().toLowerCase().contains(search);
                    boolean matchesPhone = client.getPhone() != null
                            && client.getPhone().toLowerCase().contains(search);
                    return matchesName || matchesEmail || matchesPhone;
                })
                .toList();

        List<Map<String, Object>> rows = clients.stream()
                .map(client -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("name", client.getName());
                    row.put("email", client.getEmail());
                    row.put("phone", client.getPhone());
                    row.put("createdAt", client.getCreatedAt());
                    row.put("updatedAt", client.getUpdatedAt());

                    return row;

                })
                .toList();

        return buildResponse(
                "Client Summary Report",
                List.of("name", "email", "phone", "createdAt", "updatedAt"),
                rows
        );
    }
}