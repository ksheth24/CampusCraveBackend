package com.campuscravebackend.campuscravebackend.controller;

import com.campuscravebackend.campuscravebackend.dto.ListingInfo;
import com.campuscravebackend.campuscravebackend.dto.ListingPreview;
import com.campuscravebackend.campuscravebackend.entity.Listing;
import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.service.ListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listing")
public class ListingController {
    final private ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    public record CreateListingRequest(String title, String description, String ingredients, double price, String pickUpLocation, MultipartFile photo) {}

    @PostMapping("/create")
    public Listing createListing(Authentication authentication, @ModelAttribute CreateListingRequest listingRequest) {
        return listingService.createListing(authentication, listingRequest.title, listingRequest.description, listingRequest.ingredients, listingRequest.price, listingRequest.pickUpLocation, listingRequest.photo);
    }

    @GetMapping("/getListings")
    public List<ListingPreview> getListings() {
        return listingService.getListings();
    }

    @GetMapping("/getSellerListings")
    public List<ListingPreview> getSellerListings(Authentication authentication) {
        return listingService.getSellerListings(authentication);
    }

    @GetMapping("/get/{id}")
    public ListingInfo getListingInfo(@PathVariable Long id) {
        return listingService.getListingInfo(id);
    }


}
