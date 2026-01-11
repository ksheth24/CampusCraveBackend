package com.campuscravebackend.campuscravebackend.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter
@Setter
// These autogenerate code at runtime
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    // This generates the ID for a User on Insert
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true, name = "username")
    private String username;

    @Column(nullable = false, unique = true, name = "emailId")
    private String emailId;

    // This stores the hashed password
    @Column(nullable = false, name = "passwordHash")
    private String passwordHash;

    @Column(nullable = false, name = "accountVerified")
    private boolean accountVerified;

    @Column(nullable = false, name = "verifiedSeller")
    private boolean verifiedSeller;
}
