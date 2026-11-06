package com.hotel.booking.dto.response;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.domain.enums.Amenity;
import com.hotel.booking.domain.valueobject.Location;

import java.util.List;
import java.util.Set;

public record PropertyResponse(
        Long id,
        String name,
        String description,
        Location location,
        int starRating,
        Set<Amenity> amenities,
        String ownerName,
        List<RoomResponse> rooms
) {
    public static PropertyResponse from(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getName(),
                property.getDescription(),
                property.getLocation(),
                property.getStarRating(),
                property.getAmenities(),
                property.getOwner().getName(),
                property.getRooms().stream().map(RoomResponse::from).toList()
        );
    }
}
