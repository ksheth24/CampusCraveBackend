package com.campuscravebackend.campuscravebackend.exception;

public class ReservationNotAllowed extends RuntimeException {
    public ReservationNotAllowed(String message) {
        super(message);
    }
}
