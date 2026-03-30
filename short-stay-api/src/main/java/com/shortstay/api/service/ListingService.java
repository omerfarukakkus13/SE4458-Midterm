package com.shortstay.api.service;

import com.shortstay.api.dto.request.ListingRequest;
import com.shortstay.api.entity.Listing;
import com.shortstay.api.entity.User;
import com.shortstay.api.repository.ListingRepository;
import com.shortstay.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = {"listings", "listingsByCity"}, allEntries = true)
    public Listing createListing(ListingRequest request, String username) {
        User managedHost = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Listing listing = new Listing();
        listing.setNoOfPeople(request.getNoOfPeople());
        listing.setCountry(request.getCountry());
        listing.setCity(request.getCity());
        listing.setPrice(request.getPrice());
        listing.setHost(managedHost);

        return listingRepository.save(listing);
    }

    @Cacheable(value = "listings")
    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    @Cacheable(value = "listingsByCity", key = "#city")
    public List<Listing> getListingsByCity(String city) {
        return listingRepository.findByCityIgnoreCase(city);
    }

    @Transactional
    @CacheEvict(value = {"listings", "listingsByCity"}, allEntries = true)
    public Listing updateListing(Long id, ListingRequest request, String username) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + id));

        if (!listing.getHost().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this listing!");
        }

        listing.setNoOfPeople(request.getNoOfPeople());
        listing.setCity(request.getCity());
        listing.setCountry(request.getCountry());
        listing.setPrice(request.getPrice());

        return listingRepository.save(listing);
    }

    @Transactional
    @CacheEvict(value = {"listings", "listingsByCity"}, allEntries = true)
    public void deleteListing(Long id, String username) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + id));

        if (!listing.getHost().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this listing!");
        }

        listingRepository.delete(listing);
    }
}