package com.shortstay.api.controller;

import com.shortstay.api.dto.request.ReservationRequest;
import com.shortstay.api.dto.response.ReservationResponse;
import com.shortstay.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ReservationResponse response = reservationService.createReservation(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reservationService.getMyReservations(username));
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<ReservationResponse>> getListingReservations(@PathVariable Long listingId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reservationService.getListingReservations(listingId, username));
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reservationService.cancelReservation(id, username));
    }
}