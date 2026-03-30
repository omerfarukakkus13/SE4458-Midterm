package com.shortstay.api.dto.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long reservationId;
    private Integer rating;
    private String comment;
}