package com.campuscravebackend.campuscravebackend.service;

import com.campuscravebackend.campuscravebackend.controller.ListingController;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListingService {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    public Listing createListing(Authentication authentication, String title, String description, String ingredients, Double price, String pickupLoc) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticated("Not Authenticated");
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setIngredients(ingredients);
        listing.setPhotoUrl("test");
        listing.setPrice(price);
        listing.setPickupLoc(pickupLoc);
        listing.setSellerId(user.getId());
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
//
//    public List<ListingPreview> getSellerListings() {
//
//    }

}
