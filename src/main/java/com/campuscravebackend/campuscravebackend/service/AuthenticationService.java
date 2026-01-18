package com.campuscravebackend.campuscravebackend.service;


import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.exception.EmailAlreadyExists;
import com.campuscravebackend.campuscravebackend.exception.InvalidLoginCredentials;
import com.campuscravebackend.campuscravebackend.exception.PasswordsDontMatch;
import com.campuscravebackend.campuscravebackend.exception.UserNameAlreadyExists;
import com.campuscravebackend.campuscravebackend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final EmailService emailService;


    public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder encoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.emailService = emailService;
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

    public void sendVerificationEmail(String email) {
        Random rand = new Random();
        int code = rand.nextInt(900000) + 100000;
        String verificationCode = "" + code; // or generate here if needed

        String htmlBody = """
        <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    background-color: #f9fafb; padding: 40px;">
          <div style="max-width: 480px; margin: 0 auto; background-color: #ffffff;
                      border-radius: 8px; padding: 32px;
                      box-shadow: 0 4px 12px rgba(0,0,0,0.05);">

            <h2 style="margin-top: 0; color: #111827;">Verify your email</h2>

            <p style="color: #374151; font-size: 15px; line-height: 1.5;">
              Thanks for signing up for <strong>CampusCrave</strong>!  
              To finish setting up your account, please use the verification code below:
            </p>

            <div style="margin: 24px 0; padding: 16px; background-color: #f3f4f6;
                        border-radius: 6px; text-align: center;">
              <span style="font-size: 28px; font-weight: 700;
                           letter-spacing: 4px; color: #111827;">
                %s
              </span>
            </div>

            <p style="color: #374151; font-size: 14px;">
              This code will expire in <strong>10 minutes</strong>.
            </p>

            <p style="margin-top: 32px; font-size: 12px; color: #6b7280;">
              — The CampusCrave Team
            </p>

          </div>
        </div>
        """.formatted(verificationCode);

        emailService.sendEmail(email, "One step closer! Verify Your Email 🎉",
                htmlBody);

        User user = userRepository.findByEmailId(email).orElseThrow(() ->  new InvalidLoginCredentials("Invalid Login Credentials!"));
        user.setVerificationHash(verificationCode);
    }

    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmailId(email).orElseThrow(() ->  new InvalidLoginCredentials("Invalid Login Credentials!"));
        user.setVerificationHash(verificationCode);
    }
}
