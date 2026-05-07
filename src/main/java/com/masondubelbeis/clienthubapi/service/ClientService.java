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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    private final ActivityRepository activityRepository;

    public ClientService(ClientRepository clientRepository,
                         UserRepository userRepository,
                         ActivityRepository activityRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> getClients(Pageable pageable) {
        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client list request failed: authenticated user not found. email={}", email);
                    return new NotFoundException("User not found");
                });

        Page<ClientResponse> clients = clientRepository
                .findByUser(user, pageable)
                .map(this::toResponse);

        log.debug(
                "Client list retrieved. userId={}, email={}, page={}, size={}, totalElements={}",
                user.getId(),
                user.getEmail(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                clients.getTotalElements()
        );

        return clients;
    }

    @Transactional(readOnly = true)
    public ClientResponse getClient(UUID clientId) {
        Client client = getClientEntity(clientId);

        log.debug("Client retrieved. clientId={}", client.getId());

        return toResponse(client);
    }

    @Transactional(readOnly = true)
    public Client getClientEntity(UUID clientId) {
        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client lookup failed: authenticated user not found. email={}", email);
                    return new NotFoundException("User not found with email: " + email);
                });

        Client client = clientRepository
                .findByIdAndUser(clientId, user)
                .orElseThrow(() -> {
                    log.warn(
                            "Client lookup failed: client not found or not owned by user. clientId={}, userId={}, email={}",
                            clientId,
                            user.getId(),
                            user.getEmail()
                    );
                    return new NotFoundException("Client not found with id: " + clientId);
                });

        log.debug("Client lookup successful. clientId={}, userId={}", client.getId(), user.getId());

        return client;
    }

    @Transactional
    public ClientResponse createClient(ClientRequest request) throws BadRequestException {
        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client creation failed: authenticated user not found. email={}", email);
                    return new NotFoundException("User not found");
                });

        String normalizedClientEmail = normalizeEmail(request.email());

        if (normalizedClientEmail != null && clientRepository.existsByUserAndEmail(user, normalizedClientEmail)) {
            log.warn(
                    "Client creation rejected: duplicate client email. userId={}, email={}, clientEmail={}",
                    user.getId(),
                    user.getEmail(),
                    normalizedClientEmail
            );
            throw new BadRequestException("Client with this email already exists");
        }

        Client client = new Client();
        client.setName(request.name());
        client.setEmail(normalizedClientEmail);
        client.setPhone(normalizePhone(request.phone()));
        client.setUser(user);

        Client savedClient = clientRepository.save(client);

        log.info(
                "Client created. clientId={}, userId={}, email={}",
                savedClient.getId(),
                user.getId(),
                user.getEmail()
        );

        return toResponse(savedClient);
    }

    @Transactional
    public ClientResponse updateClient(UUID id, ClientRequest request) throws BadRequestException {
        Client existingClient = getClientEntity(id);
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();

        String normalizedEmail = normalizeEmail(request.email());
        String existingEmail = normalizeEmail(existingClient.getEmail());

        if (normalizedEmail != null && !normalizedEmail.equals(existingEmail)) {
            boolean emailExists = clientRepository.existsByUserAndEmail(existingClient.getUser(), normalizedEmail);

            if (emailExists) {
                log.warn(
                        "Client update rejected: duplicate client email. clientId={}, currentUserEmail={}, clientEmail={}",
                        existingClient.getId(),
                        currentUserEmail,
                        normalizedEmail
                );
                throw new BadRequestException("Client with this email already exists");
            }
        }

        existingClient.setName(request.name());
        existingClient.setEmail(normalizedEmail);
        existingClient.setPhone(normalizePhone(request.phone()));

        Client updatedClient = clientRepository.save(existingClient);

        log.info(
                "Client updated. clientId={}, currentUserEmail={}",
                updatedClient.getId(),
                currentUserEmail
        );

        return toResponse(updatedClient);
    }

    @Transactional
    public void deleteClient(UUID id) throws BadRequestException {
        Client existingClient = getClientEntity(id);
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();

        if (activityRepository.existsByClient(existingClient)) {
            log.warn(
                    "Client deletion rejected: client has existing activities. clientId={}, currentUserEmail={}",
                    existingClient.getId(),
                    currentUserEmail
            );
            throw new BadRequestException("Cannot delete a client with existing activities.");
        }

        clientRepository.deleteById(id);

        log.info(
                "Client deleted. clientId={}, currentUserEmail={}",
                existingClient.getId(),
                currentUserEmail
        );
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

    private String normalizeEmail(String email) {
        return email == null || email.isBlank() ? null : email.trim().toLowerCase();
    }

    private String normalizePhone(String phone) throws BadRequestException {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        String digitsOnly = phone.replaceAll("\\D", "");

        if (digitsOnly.length() != 10) {
            log.warn("Phone normalization failed: invalid phone length. digitsLength={}", digitsOnly.length());
            throw new BadRequestException("Phone number must contain exactly 10 digits.");
        }

        return digitsOnly.substring(0, 3) + "-"
                + digitsOnly.substring(3, 6) + "-"
                + digitsOnly.substring(6);
    }
}