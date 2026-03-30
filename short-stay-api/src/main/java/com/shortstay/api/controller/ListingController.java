package com.shortstay.api.controller;

import com.shortstay.api.dto.request.ListingRequest;
import com.shortstay.api.dto.response.ListingResponse;
import com.shortstay.api.entity.Listing;
import com.shortstay.api.entity.User;
import com.shortstay.api.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    public ResponseEntity<ListingResponse> createListing(@RequestBody ListingRequest request) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();


        String username = auth.getName();
        Listing listing = listingService.createListing(request, username);
        return ResponseEntity.ok(convertToResponse(listing));
    }

    @GetMapping
    public ResponseEntity<List<ListingResponse>> getAllListings() {
        List<ListingResponse> response = listingService.getAllListings()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ListingResponse>> searchByCity(@RequestParam String city) {
        List<ListingResponse> response = listingService.getListingsByCity(city)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        listingService.deleteListing(id, username);
        return ResponseEntity.ok("Listing deleted successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> updateListing(
            @PathVariable Long id,
            @RequestBody ListingRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Listing listing = listingService.updateListing(id, request, username);
        return ResponseEntity.ok(convertToResponse(listing));
    }

    private ListingResponse convertToResponse(Listing listing) {
        return ListingResponse.builder()
                .id(listing.getId())
                .noOfPeople(listing.getNoOfPeople())
                .city(listing.getCity())
                .country(listing.getCountry())
                .price(listing.getPrice())
                .hostName(listing.getHost().getUsername())
                .build();
    }
}