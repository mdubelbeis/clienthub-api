package com.masondubelbeis.clienthubapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Setter
@Getter
public class Client {

    @Id
    @GeneratedValue
    @Column(name="id")
    private UUID id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="email", unique = true)
    private String email;

    @Column(name="phone")
    private String phone;

    @Column(name="created_at")
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "client")
    private List<Activity> activities;
}