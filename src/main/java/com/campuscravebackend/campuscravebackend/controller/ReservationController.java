package com.campuscravebackend.campuscravebackend.controller;

import com.campuscravebackend.campuscravebackend.dto.ReservationInfo;
import com.campuscravebackend.campuscravebackend.service.ReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public record UpdateReservationStatusRequest(String status) {}

    @PostMapping("/create/{listingId}")
    public ReservationInfo createReservation(Authentication authentication, @PathVariable Long listingId) {
        return reservationService.createReservation(authentication, listingId);
    }

    @GetMapping("/myReservations")
    public List<ReservationInfo> getBuyerReservations(Authentication authentication) {
        return reservationService.getBuyerReservations(authentication);
    }

    @GetMapping("/sellerReservations")
    public List<ReservationInfo> getSellerReservations(Authentication authentication) {
        return reservationService.getSellerReservations(authentication);
    }

    @PatchMapping("/{reservationId}/status")
    public ReservationInfo updateReservationStatus(
            Authentication authentication,
            @PathVariable Long reservationId,
            @RequestBody UpdateReservationStatusRequest request
    ) {
        return reservationService.updateReservationStatus(authentication, reservationId, request.status);
    }
}
