package com.campuscravebackend.campuscravebackend.dto;

import com.campuscravebackend.campuscravebackend.entity.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationInfo(
        Long id,
        Long listingId,
        String listingTitle,
        String listingPhoto,
        Double listingPrice,
        String pickupLocation,
        Long buyerId,
        String buyerUsername,
        Long sellerId,
        String sellerUsername,
        ReservationStatus status,
        LocalDateTime reservedAt,
        LocalDateTime updatedAt
) {}
