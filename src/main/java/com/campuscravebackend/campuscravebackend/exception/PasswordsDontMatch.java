package com.campuscravebackend.campuscravebackend.exception;

public class PasswordsDontMatch extends RuntimeException {
    public PasswordsDontMatch(String message) {
        super(message);
    }
}
