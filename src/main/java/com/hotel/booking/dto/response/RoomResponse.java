package com.hotel.booking.dto.response;

import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.enums.RoomType;

import java.math.BigDecimal;

public record RoomResponse(
        Long id,
        String roomNumber,
        RoomType roomType,
        BigDecimal pricePerNight,
        int capacity,
        boolean available
) {
    public static RoomResponse from(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.getCapacity(),
                room.getAvailable()
        );
    }
}
