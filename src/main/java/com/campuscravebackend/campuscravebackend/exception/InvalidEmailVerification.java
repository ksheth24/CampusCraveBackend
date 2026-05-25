package com.campuscravebackend.campuscravebackend.exception;

public class InvalidEmailVerification extends RuntimeException {
    public InvalidEmailVerification(String message) {
        super(message);
    }
}
