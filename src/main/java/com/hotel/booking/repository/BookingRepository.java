package com.hotel.booking.repository;

import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByGuestEmail(String guestEmail);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByRoomPropertyId(Long propertyId);
}
