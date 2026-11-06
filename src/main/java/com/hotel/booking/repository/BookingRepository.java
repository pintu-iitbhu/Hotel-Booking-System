package com.hotel.booking.repository;

import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.room r JOIN FETCH r.property WHERE b.id = :id")
    Optional<Booking> findById(@Param("id") Long id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.room r JOIN FETCH r.property WHERE b.guestEmail = :email")
    List<Booking> findByGuestEmail(@Param("email") String email);

    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByRoomPropertyId(Long propertyId);
}
