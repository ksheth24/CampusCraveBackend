package com.campuscravebackend.campuscravebackend.entity;

public enum ReservationStatus {
    RESERVED,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED_BY_BUYER,
    CANCELLED_BY_SELLER,
    EXPIRED
}