package com.shortstay.api.repository;

import com.shortstay.api.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByListingId(Long listingId);

    Optional<Review> findByReservationId(Long reservationId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.listing.id = :listingId")
    Double getAverageRatingForListing(@org.springframework.data.repository.query.Param("listingId") Long listingId);
}