package com.shortstay.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private Long reservationId;
    private Long listingId;
    private String guestUsername;
    private Integer rating;
    private String comment;
}