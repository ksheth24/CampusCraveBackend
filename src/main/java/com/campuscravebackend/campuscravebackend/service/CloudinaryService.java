package com.campuscravebackend.campuscravebackend.service;

import com.campuscravebackend.campuscravebackend.exception.ImageUploadFailure;
import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", "campuscrave/listings",
                            "resource_type", "image"
                    )
            );

            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace(); // 👈 TEMPORARY
            throw new ImageUploadFailure("Image upload failed: " + e.getMessage());
        }
    }
    
}
