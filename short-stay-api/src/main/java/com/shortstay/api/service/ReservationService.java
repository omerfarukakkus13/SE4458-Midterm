package com.shortstay.api.service;

import com.shortstay.api.dto.request.ReservationRequest;
import com.shortstay.api.dto.response.ReservationResponse;
import com.shortstay.api.entity.Listing;
import com.shortstay.api.entity.Reservation;
import com.shortstay.api.entity.ReservationStatus;
import com.shortstay.api.entity.User;
import com.shortstay.api.repository.ListingRepository;
import com.shortstay.api.repository.ReservationRepository;
import com.shortstay.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, String username) {

        LocalDate now = LocalDate.now();
        if (request.getCheckInDate().isBefore(now) || request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new IllegalArgumentException("Invalid reservation dates!");
        }

        boolean isBooked = reservationRepository.existsConflictingReservation(
                request.getListingId(), request.getCheckInDate(), request.getCheckOutDate());

        if (isBooked) {
            throw new IllegalStateException("Listing is already booked for these dates!");
        }

        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found!"));
        User guest = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (listing.getHost().getUsername().equals(username)) {
            throw new IllegalStateException("You cannot book your own listing!");
        }

        long daysBetween = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (daysBetween == 0) daysBetween = 1;
        double totalPrice = daysBetween * listing.getPrice();


        Reservation reservation = Reservation.builder()
                .listing(listing)
                .guest(guest)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(ReservationStatus.CONFIRMED) // Varsayılan olarak onaylı yapalım
                .build();

        reservation = reservationRepository.save(reservation);

        return convertToResponse(reservation);
    }

    private ReservationResponse convertToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .listingId(reservation.getListing().getId())
                .guestUsername(reservation.getGuest().getUsername())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .build();
    }

    public java.util.List<ReservationResponse> getMyReservations(String username) {
        return reservationRepository.findByGuestUsername(username).stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<ReservationResponse> getListingReservations(Long listingId, String username) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found!"));

        if (!listing.getHost().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to view these reservations!");
        }

        return reservationRepository.findByListingId(listingId).stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    @Transactional
    public ReservationResponse cancelReservation(Long reservationId, String username) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found!"));

        boolean isGuest = reservation.getGuest().getUsername().equals(username);
        boolean isHost = reservation.getListing().getHost().getUsername().equals(username);

        if (!isGuest && !isHost) {
            throw new RuntimeException("You are not authorized to cancel this reservation!");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled!");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return convertToResponse(reservationRepository.save(reservation));
    }
}