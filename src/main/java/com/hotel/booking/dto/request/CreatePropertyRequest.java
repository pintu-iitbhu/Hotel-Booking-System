package com.hotel.booking.dto.request;

import com.hotel.booking.domain.enums.Amenity;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

public record CreatePropertyRequest(
        @NotBlank String name,
        String description,
        @NotBlank String address,
        @NotBlank String locality,
        @NotBlank String city,
        @NotBlank String country,
        @Min(1) @Max(5) int starRating,
        Set<Amenity> amenities,
        @NotEmpty List<CreateRoomRequest> rooms
) {}
