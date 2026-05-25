package com.campuscravebackend.campuscravebackend.repository;

import com.campuscravebackend.campuscravebackend.entity.Reservation;
import com.campuscravebackend.campuscravebackend.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByListingIdAndStatus(Long listingId, ReservationStatus status);
    Optional<Reservation> findByListingIdAndStatusIn(Long listingId, List<ReservationStatus> statuses);
    boolean existsByListingIdAndStatus(Long listingId, ReservationStatus status);
    boolean existsByListingIdAndStatusIn(Long listingId, List<ReservationStatus> statuses);
    List<Reservation> findByBuyerUsername(String username);
    List<Reservation> findByListingSellerUsername(String username);
}
