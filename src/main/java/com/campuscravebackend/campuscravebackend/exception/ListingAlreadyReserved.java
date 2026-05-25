package com.campuscravebackend.campuscravebackend.exception;

public class ListingAlreadyReserved extends RuntimeException {
    public ListingAlreadyReserved(String message) {
        super(message);
    }
}
