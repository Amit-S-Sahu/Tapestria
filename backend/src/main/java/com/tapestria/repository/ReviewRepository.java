package com.tapestria.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tapestria.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByIsbn(String isbn);
    List<Review> findByEmail(String email);
    List<Review> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);
}
