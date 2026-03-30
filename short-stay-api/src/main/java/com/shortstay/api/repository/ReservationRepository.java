package com.shortstay.api.repository;

import com.shortstay.api.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByGuestUsername(String username);
    List<Reservation> findByListingId(Long listingId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
            "WHERE r.listing.id = :listingId AND r.status != 'CANCELLED' " +
            "AND (r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate)")
    boolean existsConflictingReservation(@Param("listingId") Long listingId,
                                         @Param("checkInDate") LocalDate checkInDate,
                                         @Param("checkOutDate") LocalDate checkOutDate);
}