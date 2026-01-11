package com.campuscravebackend.campuscravebackend.controller;

import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.security.JwtUtil;
import com.campuscravebackend.campuscravebackend.service.RegisterService;
import jakarta.persistence.GeneratedValue;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {
    private final RegisterService registerService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public RegisterController(RegisterService registerService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.registerService = registerService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public record RegisterRequest(String username, String email, String password, String passwordConfirm) {}

    public record LoginRequest(String username, String password) {}

    public record VerifyEmailRequest(String email) {}

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return registerService.registerService(request.username, request.email, request.password, request.passwordConfirm);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username, request.password
                )
        );
        String token =  jwtUtil.generateToken(request.username);
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // true in production (HTTPS)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Login successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // true in production (HTTPS)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(Map.of("message", "Logged out"));
    }


//    @PostMapping("/verifyEmail")
//    public void verifyEmail(@RequestBody VerifyEmailRequest request) {
//        registerService.sendVerificationEmail(request.email());
//    }

    @GetMapping("/check")
    public ResponseEntity<?> check(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(Map.of("username", authentication.getName()));
    }


}
