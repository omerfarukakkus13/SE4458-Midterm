package com.shortstay.api.controller;

import com.shortstay.api.dto.request.ReviewRequest;
import com.shortstay.api.dto.response.ReviewResponse;
import com.shortstay.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reviewService.createReview(request, username));
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<ReviewResponse>> getListingReviews(@PathVariable Long listingId) {
        return ResponseEntity.ok(reviewService.getListingReviews(listingId));
    }
}