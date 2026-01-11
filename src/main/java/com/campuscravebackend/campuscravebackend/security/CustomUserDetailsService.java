package com.campuscravebackend.campuscravebackend.security;


import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.exception.InvalidLoginCredentials;
import com.campuscravebackend.campuscravebackend.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws InvalidLoginCredentials {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidLoginCredentials("Invalid Login Credentials"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),   // 🔐 HASHED password from DB
                List.of()             // roles later
        );
    }
}
