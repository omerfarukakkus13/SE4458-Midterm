package com.shortstay.api.service;

import com.shortstay.api.dto.request.ReviewRequest;
import com.shortstay.api.dto.response.ReviewResponse;
import com.shortstay.api.entity.Reservation;
import com.shortstay.api.entity.ReservationStatus;
import com.shortstay.api.entity.Review;
import com.shortstay.api.repository.ReservationRepository;
import com.shortstay.api.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, String username) {

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5!");
        }

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found!"));

        if (!reservation.getGuest().getUsername().equals(username)) {
            throw new RuntimeException("Only those who book a stay can review!");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("You cannot review a cancelled reservation!");
        }

        if (reviewRepository.findByReservationId(reservation.getId()).isPresent()) {
            throw new IllegalStateException("You have already reviewed this stay!");
        }

        Review review = Review.builder()
                .reservation(reservation)
                .listing(reservation.getListing())
                .guest(reservation.getGuest())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);

        return convertToResponse(review);
    }

    public List<ReviewResponse> getListingReviews(Long listingId) {
        return reviewRepository.findByListingId(listingId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse convertToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reservationId(review.getReservation().getId())
                .listingId(review.getListing().getId())
                .guestUsername(review.getGuest().getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }
}