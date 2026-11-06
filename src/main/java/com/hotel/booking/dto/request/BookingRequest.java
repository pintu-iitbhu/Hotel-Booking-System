package com.hotel.booking.dto.request;

import com.hotel.booking.domain.enums.RoomType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull Long propertyId,
        @NotNull RoomType roomType,
        @NotBlank String guestName,
        @NotBlank String guestEmail,
        String guestPhone,
        @Min(1) int numGuests,
        @NotNull @FutureOrPresent LocalDate checkInDate,
        @NotNull @Future LocalDate checkOutDate,
        String specialRequests
) {}
