package com.shortstay.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListingReportResponse {
    private Long id;
    private String country;
    private String city;
    private Double price;
    private Double averageRating;
}