package com.hotel.booking.repository;

import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.enums.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"room", "room.property"})
    Optional<Booking> findById(Long id);

    @EntityGraph(attributePaths = {"room", "room.property"})
    List<Booking> findByGuestEmail(String guestEmail);

    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByRoomPropertyId(Long propertyId);
}
