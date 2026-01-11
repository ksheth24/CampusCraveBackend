package com.campuscravebackend.campuscravebackend.exception;

public class InvalidLoginCredentials extends RuntimeException {
    public InvalidLoginCredentials(String message) {
        super(message);
    }
}
