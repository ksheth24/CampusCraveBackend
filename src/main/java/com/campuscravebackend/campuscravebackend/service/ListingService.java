package com.campuscravebackend.campuscravebackend.service;

import com.campuscravebackend.campuscravebackend.controller.ListingController;
import com.campuscravebackend.campuscravebackend.dto.ListingInfo;
import com.campuscravebackend.campuscravebackend.dto.ListingPreview;
import com.campuscravebackend.campuscravebackend.entity.Listing;
import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.exception.InvalidLoginCredentials;
import com.campuscravebackend.campuscravebackend.exception.NotAuthenticated;
import com.campuscravebackend.campuscravebackend.repository.ListingRepository;
import com.campuscravebackend.campuscravebackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.multi.MultiPanelUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListingService {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Listing createListing(Authentication authentication, String title, String description, String ingredients, Double price, String pickupLoc, MultipartFile photo) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticated("Not Authenticated");
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        String photoUrl = cloudinaryService.uploadImage(photo);

        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setIngredients(ingredients);
        listing.setPhotoUrl(photoUrl);
        listing.setPrice(price);
        listing.setPickupLoc(pickupLoc);
        listing.setSeller(user);
        listing.setPhotoUrl(photoUrl);
        return listingRepository.save(listing);
    }

    public List<ListingPreview> getListings() {
        return listingRepository.findAll()
                .stream()
                .map(l -> new ListingPreview(
                        l.getId(),
                        l.getTitle(),
                        l.getPrice(),
                        l.getPickupLoc()
        )).toList();
    }

    public List<ListingPreview> getSellerListings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticated("Not Authenticated");
        }
        String username = authentication.getName();
        return listingRepository.findBySellerUsername(username)
                .stream()
                .map(l -> new ListingPreview(
                        l.getId(),
                        l.getTitle(),
                        l.getPrice(),
                        l.getPickupLoc()
                )).toList();
    }

    public ListingInfo getListingInfo(Long listingId) {
        return listingRepository.findById(listingId)
                .map(l -> new ListingInfo(
                        l.getId(),
                        l.getTitle(),
                        l.getDescription(),
                        l.getIngredients(),
                        l.getPrice(),
                        l.getPickupLoc()
                )).orElseThrow();
    }



}
