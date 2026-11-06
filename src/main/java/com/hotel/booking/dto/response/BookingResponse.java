package com.hotel.booking.dto.response;

import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        String guestName,
        String guestEmail,
        int numGuests,
        Long propertyId,
        String propertyName,
        String roomNumber,
        String roomType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        long nights,
        BigDecimal totalPrice,
        BookingStatus status,
        LocalDateTime createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getNumGuests(),
                booking.getRoom().getProperty().getId(),
                booking.getRoom().getProperty().getName(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getRoomType().name(),
                booking.getDateRange().getCheckInDate(),
                booking.getDateRange().getCheckOutDate(),
                booking.getDateRange().nights(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
