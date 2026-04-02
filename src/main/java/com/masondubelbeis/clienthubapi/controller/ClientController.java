package com.masondubelbeis.clienthubapi.controller;

import com.masondubelbeis.clienthubapi.dto.request.ClientRequest;
import com.masondubelbeis.clienthubapi.dto.response.ClientResponse;
import com.masondubelbeis.clienthubapi.service.ClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public Page<ClientResponse> getClients(Pageable pageable) {
        return clientService.getClients(pageable);
    }

    @GetMapping("/{id}")
    public ClientResponse getClient(@PathVariable UUID id) {
        return clientService.getClient(id);
    }

    @PostMapping
    public ClientResponse createClient(@Valid @RequestBody ClientRequest request) throws BadRequestException {
        return clientService.createClient(request);
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
    }
}