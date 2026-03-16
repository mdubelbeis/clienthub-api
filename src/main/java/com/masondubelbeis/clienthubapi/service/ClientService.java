package com.masondubelbeis.clienthubapi.service;

import com.masondubelbeis.clienthubapi.dto.request.ClientRequest;
import com.masondubelbeis.clienthubapi.dto.response.ClientResponse;
import com.masondubelbeis.clienthubapi.exception.NotFoundException;
import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.ClientRepository;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public Page<ClientResponse> getClients(Pageable pageable) {

        User user = userRepository.findAll().getFirst();

        return clientRepository.findByUser(user, pageable)
                .map(client -> new ClientResponse(
                        client.getId(),
                        client.getName(),
                        client.getEmail(),
                        client.getPhone(),
                        client.getCreatedAt()
                ));
    }

    public Client getClient(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Client not found"));
    }

    @Transactional
    public ClientResponse createClient(ClientRequest request) {

        User user = userRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No users exist in database"));

        Client client = new Client();
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setUser(user);

        Client saved = clientRepository.save(client);

        return new ClientResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public Client updateClient(Client client) {
        return clientRepository.save(client);
    }

    @Transactional
    public void deleteClient(UUID id) {
        clientRepository.deleteById(id);
    }
}