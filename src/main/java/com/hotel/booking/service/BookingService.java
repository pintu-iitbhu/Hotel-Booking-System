package com.hotel.booking.service;

import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.dto.request.BookingRequest;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingRequest request);
    Booking findById(Long id);
    Booking checkIn(Long bookingId);
    Booking checkOut(Long bookingId);
    Booking cancel(Long bookingId);
    List<Booking> findByGuestEmail(String email);
}
