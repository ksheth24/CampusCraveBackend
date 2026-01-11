package com.campuscravebackend.campuscravebackend.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "listing")
@Getter
@Setter
// These autogenerate code at runtime
@NoArgsConstructor
@AllArgsConstructor

public class Listing {
    @Id
    // This generates the ID for a User on Insert
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "title")
    private String title;

    @Column(nullable = false, name = "description")
    private String description;

    @Column(nullable = false, name = "ingredients")
    private String ingredients;

    @Column(nullable = false, name = "photo_url")
    private String photoUrl;

    @Column(nullable = false, name = "price")
    private Double price;

    @Column(nullable = false, name = "pickupLoc")
    private String pickupLoc;

    @Column(nullable = false, name = "sellerId")
    private Long sellerId;
}
