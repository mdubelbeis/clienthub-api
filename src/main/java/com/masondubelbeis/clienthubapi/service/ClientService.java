package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ClientRequest;
import com.masondubelbeis.clienthubapi.dto.response.ClientResponse;
import com.masondubelbeis.clienthubapi.exception.NotFoundException;
import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.ActivityRepository;
import com.masondubelbeis.clienthubapi.repository.ClientRepository;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import com.masondubelbeis.clienthubapi.security.SecurityUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;
    private final ActivityRepository activityRepository;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository, ActivityService activityService, ActivityRepository activityRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
        this.activityRepository = activityRepository;
    }

    public Page<ClientResponse> getClients(Pageable pageable) {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return clientRepository
                .findByUser(user, pageable)
                .map(this::toResponse);
    }

    public ClientResponse getClient(UUID clientId) {
        Client client = getClientEntity(clientId);

        return toResponse(client);
    }

    public Client getClientEntity(UUID clientId) {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        return clientRepository
                .findByIdAndUser(clientId, user)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + clientId));
    }

    @Transactional
    public ClientResponse createClient(ClientRequest request) throws BadRequestException {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (clientRepository.existsByUserAndEmail(user, request.email())) {
            throw new BadRequestException("Client with this email already exists");
        }

        Client client = new Client();
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setUser(user);

        clientRepository.save(client);

        return toResponse(client);
    }

    @Transactional
    public ClientResponse updateClient(UUID id, ClientRequest request) throws BadRequestException {
        Client existingClient = getClientEntity(id);

        String normalizedEmail = request.email() == null ? null : request.email().trim().toLowerCase();

        if (normalizedEmail != null && !normalizedEmail.equalsIgnoreCase(existingClient.getEmail())) {
            boolean emailExists = clientRepository.existsByUserAndEmail(existingClient.getUser(), normalizedEmail);
            if (emailExists) {
                throw new BadRequestException("Client with this email already exists");
            }
        }

        existingClient.setName(request.name());
        existingClient.setEmail(normalizedEmail);
        existingClient.setPhone(request.phone());

        Client updatedClient = clientRepository.save(existingClient);

        return toResponse(updatedClient);
    }

    @Transactional
    public void deleteClient(UUID id) throws BadRequestException {
        Client existingClient = getClientEntity(id);

        if (activityRepository.existsByClient(existingClient)) {
            throw new BadRequestException("Cannot delete a client with existing activities.");
        }

        clientRepository.deleteById(id);
    }

    private ClientResponse toResponse(Client client) {

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}