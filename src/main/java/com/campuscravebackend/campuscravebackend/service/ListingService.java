package com.campuscravebackend.campuscravebackend.service;

import com.campuscravebackend.campuscravebackend.controller.ListingController;
import com.campuscravebackend.campuscravebackend.dto.ListingInfo;
import com.campuscravebackend.campuscravebackend.dto.ListingPreview;
import com.campuscravebackend.campuscravebackend.entity.Listing;
import com.campuscravebackend.campuscravebackend.entity.Reservation;
import com.campuscravebackend.campuscravebackend.entity.ReservationStatus;
import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.exception.InvalidLoginCredentials;
import com.campuscravebackend.campuscravebackend.exception.NotAuthenticated;
import com.campuscravebackend.campuscravebackend.repository.ListingRepository;
import com.campuscravebackend.campuscravebackend.repository.ReservationRepository;
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
    private static final List<ReservationStatus> ACTIVE_RESERVATION_STATUSES = List.of(
            ReservationStatus.RESERVED,
            ReservationStatus.CONFIRMED,
            ReservationStatus.PREPARING,
            ReservationStatus.READY_FOR_PICKUP
    );

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final CloudinaryService cloudinaryService;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository, ReservationRepository reservationRepository, CloudinaryService cloudinaryService) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
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
        return listingRepository.findAllUnreserved(ACTIVE_RESERVATION_STATUSES)
                .stream()
                .map(l -> new ListingPreview(
                        l.getId(),
                        l.getTitle(),
                        l.getPrice(),
                        l.getPickupLoc(),
                        l.getPhotoUrl(),
                        l.getPhotoUrl(),
                        false
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
                        l.getPickupLoc(),
                        l.getPhotoUrl(),
                        l.getPhotoUrl(),
                        reservationRepository.existsByListingIdAndStatusIn(l.getId(), ACTIVE_RESERVATION_STATUSES)
                )).toList();
    }

    public ListingInfo getListingInfo(Long listingId) {
        return listingRepository.findById(listingId)
                .map(l -> {
                    Reservation reservation = reservationRepository
                            .findByListingIdAndStatusIn(l.getId(), ACTIVE_RESERVATION_STATUSES)
                            .orElse(null);

                    return new ListingInfo(
                            l.getId(),
                            l.getTitle(),
                            l.getDescription(),
                            l.getIngredients(),
                            l.getPrice(),
                            l.getPickupLoc(),
                            l.getPhotoUrl(),
                            reservation != null,
                            reservation == null ? null : reservation.getId()
                    );
                }).orElseThrow();
    }



}
