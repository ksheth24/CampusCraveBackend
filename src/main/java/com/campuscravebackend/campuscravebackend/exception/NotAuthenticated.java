package com.campuscravebackend.campuscravebackend.exception;

public class NotAuthenticated extends RuntimeException {
    public NotAuthenticated(String message) {
        super(message);
    }
}
