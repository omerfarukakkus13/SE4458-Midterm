package com.shortstay.api.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.shortstay.api.entity.Listing;
import com.shortstay.api.dto.response.ListingReportResponse;
import com.shortstay.api.entity.User;
import com.shortstay.api.repository.ListingRepository;
import com.shortstay.api.repository.ReviewRepository;
import com.shortstay.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ListingRepository listingRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public Page<ListingReportResponse> getListingsReport(String country, String city, int page, int size, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access Denied: You must be an ADMIN to view this report!");
        }

        Pageable pageable = PageRequest.of(page, size);

        return listingRepository.findByCountryIgnoreCaseAndCityIgnoreCase(country, city, pageable)
                .map(listing -> {
                    Double avgRating = reviewRepository.getAverageRatingForListing(listing.getId());
                    return ListingReportResponse.builder()
                            .id(listing.getId())
                            .country(listing.getCountry())
                            .city(listing.getCity())
                            .price(listing.getPrice())
                            .averageRating(avgRating)
                            .build();
                });
    }
    @org.springframework.transaction.annotation.Transactional
    public String processListingFile(MultipartFile file, String username) {

        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!admin.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access Denied: You must be an ADMIN to upload this file!");
        }

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty!");
        }

        List<Listing> listings = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {

                if (isFirstLine) {
                    isFirstLine = false;
                    if (line.toLowerCase().contains("country") || line.toLowerCase().contains("city")) continue;
                }

                String[] data = line.split(",");

                if (data.length >= 4) {
                    Listing listing = new Listing();
                    listing.setNoOfPeople(Integer.parseInt(data[0].trim()));
                    listing.setCountry(data[1].trim());
                    listing.setCity(data[2].trim());
                    listing.setPrice(Double.parseDouble(data[3].trim()));
                    listing.setHost(admin);

                    listings.add(listing);
                }
            }

            listingRepository.saveAll(listings);
            return "File Processing Status: SUCCESS. " + listings.size() + " listings created.";

        } catch (Exception e) {
            throw new RuntimeException("Error processing file: " + e.getMessage());
        }
    }
}