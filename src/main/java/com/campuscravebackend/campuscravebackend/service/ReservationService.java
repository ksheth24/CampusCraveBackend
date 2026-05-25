package com.campuscravebackend.campuscravebackend.service;

import com.campuscravebackend.campuscravebackend.dto.ReservationInfo;
import com.campuscravebackend.campuscravebackend.entity.Listing;
import com.campuscravebackend.campuscravebackend.entity.Reservation;
import com.campuscravebackend.campuscravebackend.entity.ReservationStatus;
import com.campuscravebackend.campuscravebackend.entity.User;
import com.campuscravebackend.campuscravebackend.exception.ListingAlreadyReserved;
import com.campuscravebackend.campuscravebackend.exception.NotAuthenticated;
import com.campuscravebackend.campuscravebackend.exception.ReservationNotAllowed;
import com.campuscravebackend.campuscravebackend.repository.ListingRepository;
import com.campuscravebackend.campuscravebackend.repository.ReservationRepository;
import com.campuscravebackend.campuscravebackend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private static final List<ReservationStatus> ACTIVE_RESERVATION_STATUSES = List.of(
            ReservationStatus.RESERVED,
            ReservationStatus.CONFIRMED,
            ReservationStatus.PREPARING,
            ReservationStatus.READY_FOR_PICKUP
    );

    private final ReservationRepository reservationRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, ListingRepository listingRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationInfo createReservation(Authentication authentication, Long listingId) {
        User buyer = getAuthenticatedUser(authentication);
        Listing listing = listingRepository.findById(listingId).orElseThrow();

        if (listing.getSeller().getId().equals(buyer.getId())) {
            throw new ReservationNotAllowed("You cannot reserve your own listing");
        }

        if (reservationRepository.existsByListingIdAndStatusIn(listingId, ACTIVE_RESERVATION_STATUSES)) {
            throw new ListingAlreadyReserved("This listing is already reserved");
        }

        Reservation reservation = new Reservation();
        reservation.setListing(listing);
        reservation.setBuyer(buyer);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setReservedAt(LocalDateTime.now());

        try {
            return toInfo(reservationRepository.saveAndFlush(reservation));
        } catch (DataIntegrityViolationException ex) {
            throw new ListingAlreadyReserved("This listing is already reserved");
        }
    }

    public List<ReservationInfo> getBuyerReservations(Authentication authentication) {
        User buyer = getAuthenticatedUser(authentication);
        return reservationRepository.findByBuyerUsername(buyer.getUsername())
                .stream()
                .map(this::toInfo)
                .toList();
    }

    public List<ReservationInfo> getSellerReservations(Authentication authentication) {
        User seller = getAuthenticatedUser(authentication);
        return reservationRepository.findByListingSellerUsername(seller.getUsername())
                .stream()
                .map(this::toInfo)
                .toList();
    }

    @Transactional
    public ReservationInfo updateReservationStatus(Authentication authentication, Long reservationId, String status) {
        User user = getAuthenticatedUser(authentication);
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        ReservationStatus newStatus = resolveRequestedStatus(user, reservation, status);

        return updateReservationStatus(user, reservation, newStatus);
    }

    private ReservationStatus resolveRequestedStatus(User user, Reservation reservation, String status) {
        if (status == null || status.isBlank()) {
            throw new ReservationNotAllowed("Reservation status is required");
        }

        String normalizedStatus = status.trim().toUpperCase();

        if ("CANCELLED".equals(normalizedStatus)) {
            if (reservation.getBuyer().getId().equals(user.getId())) {
                return ReservationStatus.CANCELLED_BY_BUYER;
            }

            if (reservation.getListing().getSeller().getId().equals(user.getId())) {
                return ReservationStatus.CANCELLED_BY_SELLER;
            }

            throw new ReservationNotAllowed("You are not allowed to cancel this reservation");
        }

        ReservationStatus newStatus = parseStatus(normalizedStatus);

        if (newStatus == ReservationStatus.CANCELLED_BY_BUYER || newStatus == ReservationStatus.CANCELLED_BY_SELLER) {
            throw new ReservationNotAllowed("Use CANCELLED to cancel a reservation");
        }

        return newStatus;
    }

    private ReservationInfo updateReservationStatus(User user, Reservation reservation, ReservationStatus newStatus) {
        if (isFinalStatus(reservation.getStatus())) {
            throw new ReservationNotAllowed("Finalized reservations cannot be updated");
        }

        validateStatusUpdate(user, reservation, newStatus);

        reservation.setStatus(newStatus);
        reservation.setUpdatedAt(LocalDateTime.now());
        return toInfo(reservationRepository.save(reservation));
    }

    private ReservationStatus parseStatus(String status) {
        try {
            return ReservationStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new ReservationNotAllowed("Invalid reservation status");
        }
    }

    private void validateStatusUpdate(User user, Reservation reservation, ReservationStatus newStatus) {
        boolean isBuyer = reservation.getBuyer().getId().equals(user.getId());
        boolean isSeller = reservation.getListing().getSeller().getId().equals(user.getId());

        if (isBuyer && newStatus == ReservationStatus.CANCELLED_BY_BUYER) {
            return;
        }

        if (isSeller && isSellerStatus(newStatus)) {
            return;
        }

        throw new ReservationNotAllowed("You are not allowed to set this reservation status");
    }

    private boolean isSellerStatus(ReservationStatus status) {
        return status == ReservationStatus.CONFIRMED
                || status == ReservationStatus.PREPARING
                || status == ReservationStatus.READY_FOR_PICKUP
                || status == ReservationStatus.COMPLETED
                || status == ReservationStatus.CANCELLED_BY_SELLER
                || status == ReservationStatus.EXPIRED;
    }

    private boolean isFinalStatus(ReservationStatus status) {
        return status == ReservationStatus.COMPLETED
                || status == ReservationStatus.CANCELLED_BY_BUYER
                || status == ReservationStatus.CANCELLED_BY_SELLER
                || status == ReservationStatus.EXPIRED;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticated("Not Authenticated");
        }
        return userRepository.findByUsername(authentication.getName()).orElseThrow();
    }

    private ReservationInfo toInfo(Reservation reservation) {
        Listing listing = reservation.getListing();
        User buyer = reservation.getBuyer();
        User seller = listing.getSeller();

        return new ReservationInfo(
                reservation.getId(),
                listing.getId(),
                listing.getTitle(),
                listing.getPhotoUrl(),
                listing.getPrice(),
                listing.getPickupLoc(),
                buyer.getId(),
                buyer.getUsername(),
                seller.getId(),
                seller.getUsername(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getUpdatedAt()
        );
    }
}
