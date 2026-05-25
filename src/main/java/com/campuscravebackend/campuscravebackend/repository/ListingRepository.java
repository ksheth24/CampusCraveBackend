package com.campuscravebackend.campuscravebackend.repository;

// by extending Jpa you get a LOT of methods for free already
import com.campuscravebackend.campuscravebackend.entity.Listing;
import com.campuscravebackend.campuscravebackend.entity.ReservationStatus;
import com.campuscravebackend.campuscravebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findBySellerUsername(String username);

    @Query("""
            select l from Listing l
            where not exists (
                select r from Reservation r
                where r.listing = l
                and r.status in :activeStatuses
            )
            """)
    List<Listing> findAllUnreserved(@Param("activeStatuses") List<ReservationStatus> activeStatuses);
}
