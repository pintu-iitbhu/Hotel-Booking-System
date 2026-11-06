package com.hotel.booking.dto.request;

import com.hotel.booking.domain.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateRoomRequest(
        @NotBlank String roomNumber,
        @NotNull RoomType roomType,
        @NotNull @Positive BigDecimal pricePerNight,
        @Min(1) int capacity
) {}
