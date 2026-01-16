package com.campuscravebackend.campuscravebackend.config;

import org.springframework.context.annotation.Bean;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.HashMap;


@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "duc5mbign");
        config.put("api_key", "593148789266897");
        config.put("api_secret", "dbIX_Zekfy2KUbNgHI00dlASgsU");
        return new Cloudinary(config);
    }
}
