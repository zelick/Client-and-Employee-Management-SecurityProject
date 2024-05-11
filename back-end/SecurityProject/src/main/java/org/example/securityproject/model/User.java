package org.example.securityproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "username", nullable = false)
    private String username;

    public User() {}
    public User(Integer id, String username) {
        this.id = id;
        this.username = username;
    }
}
