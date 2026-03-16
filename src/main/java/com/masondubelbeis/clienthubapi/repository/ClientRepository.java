package com.masondubelbeis.clienthubapi.repository;

import com.masondubelbeis.clienthubapi.model.Client;
import com.masondubelbeis.clienthubapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Page<Client> findByUser(User user, Pageable pageable);

}