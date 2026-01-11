package com.campuscravebackend.campuscravebackend.controller;
import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.exception.EmailAlreadyExists;
import com.campuscravebackend.campuscravebackend.repository.UserRepository;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/getEmail")
    public ResponseEntity<?> getEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(Map.of("email", user.getEmailId()));
    }

    @GetMapping("/verifiedSeller")
    public ResponseEntity<?> verifiedSeller(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        boolean verifiedSeller = user.isVerifiedSeller();
        return ResponseEntity.ok(Map.of("verified", verifiedSeller));
    }
}
