package com.shortstay.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingResponse {
    private Long id;
    private Integer noOfPeople;
    private String city;
    private String country;
    private Double price;
    private String hostName;
}