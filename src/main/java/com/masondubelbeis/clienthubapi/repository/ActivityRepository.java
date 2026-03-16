package com.masondubelbeis.clienthubapi.repository;

import com.masondubelbeis.clienthubapi.model.Activity;
import com.masondubelbeis.clienthubapi.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    Page<Activity> findByClient(Client client, Pageable pageable);

}