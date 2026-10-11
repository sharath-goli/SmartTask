package com.smarttask.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="app_users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=190) private String email;
    @Column(nullable=false) private String passwordHash;
    @Column(nullable=false, length=80) private String name;
    @Column(nullable=false) private Instant createdAt = Instant.now();
    protected User() {}
    public User(String name, String email, String passwordHash) { this.name=name; this.email=email.toLowerCase(); this.passwordHash=passwordHash; }
    public Long getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public String getName(){return name;} public Instant getCreatedAt(){return createdAt;}
}
