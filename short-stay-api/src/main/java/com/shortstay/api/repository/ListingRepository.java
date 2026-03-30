package com.shortstay.api.repository;

import com.shortstay.api.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {


    List<Listing> findByCityIgnoreCase(String city);

    org.springframework.data.domain.Page<com.shortstay.api.entity.Listing> findByCountryIgnoreCaseAndCityIgnoreCase(
            String country, String city, org.springframework.data.domain.Pageable pageable);
}
