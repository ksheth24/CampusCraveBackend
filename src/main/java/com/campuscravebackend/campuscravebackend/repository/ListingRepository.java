package com.campuscravebackend.campuscravebackend.repository;

// by extending Jpa you get a LOT of methods for free already
import com.campuscravebackend.campuscravebackend.entity.Listing;
import com.campuscravebackend.campuscravebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;


public interface ListingRepository extends JpaRepository<Listing, Long> {

}
