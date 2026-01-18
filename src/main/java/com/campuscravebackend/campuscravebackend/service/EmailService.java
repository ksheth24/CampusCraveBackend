package com.campuscravebackend.campuscravebackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {
    @Value("${mailersend.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendEmail(String to, String subject, String html) {
        String url = "https://api.mailersend.com/v1/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "from", Map.of(
                        "email", "campuscrave@test-dnvo4d9e5nrg5r86.mlsender.net",
                        "name", "CampusCrave"
                ),
                "to", List.of(
                        Map.of("email", to)
                ),
                "subject", subject,
                "html", html
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }


}
