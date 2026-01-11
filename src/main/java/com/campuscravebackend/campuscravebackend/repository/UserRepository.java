package com.campuscravebackend.campuscravebackend.repository;

// by extending Jpa you get a LOT of methods for free already
import com.campuscravebackend.campuscravebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    // No implementation needed for this method. Java sees the findBy and the parameter and automatically generates SQL code
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailId(String email);

    // No implemntation needed for this method. Java sees the findBy and the parameter and automatically generates SQL code
    boolean existsByUsername(String username);
    boolean existsByEmailId(String email);
}
