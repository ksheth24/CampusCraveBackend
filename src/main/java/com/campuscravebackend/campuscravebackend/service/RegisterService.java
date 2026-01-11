package com.campuscravebackend.campuscravebackend.service;


import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.exception.EmailAlreadyExists;
import com.campuscravebackend.campuscravebackend.exception.InvalidLoginCredentials;
import com.campuscravebackend.campuscravebackend.exception.PasswordsDontMatch;
import com.campuscravebackend.campuscravebackend.exception.UserNameAlreadyExists;
import com.campuscravebackend.campuscravebackend.repository.UserRepository;
import com.campuscravebackend.campuscravebackend.security.JwtUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public RegisterService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User registerService(String username, String email, String passwordRaw, String passwordConfirm) {
        if (userRepository.existsByUsername(username)) {
            throw new UserNameAlreadyExists("Username already in use!");
        }
        if (userRepository.existsByEmailId(email)) {
            throw new EmailAlreadyExists("Email already in use");
        }
        if (!passwordRaw.equals(passwordConfirm)) {
            throw new PasswordsDontMatch("Passwords do not match!");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmailId(email);
        user.setPasswordHash(encoder.encode(passwordRaw));
        user.setAccountVerified(true);
        user.setVerifiedSeller(true);
        return userRepository.save(user);
    }

//    public void sendVerificationEmail(String email) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Test");
//        message.setText("Test Mail");
//        message.setFrom("noreply.campus.crave@gmail.com");
//
//        mailSender.send(message);
//    }
}
