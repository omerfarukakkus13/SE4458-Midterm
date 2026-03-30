package com.shortstay.api.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationRequest {
    private Long listingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}