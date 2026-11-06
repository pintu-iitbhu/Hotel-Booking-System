package com.hotel.booking.dto.response;

import com.hotel.booking.domain.entity.Owner;

public record OwnerResponse(
        Long id,
        String name,
        String email,
        String phone,
        int totalProperties
) {
    public static OwnerResponse from(Owner owner) {
        return new OwnerResponse(
                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                owner.getPhone(),
                owner.getProperties().size()
        );
    }
}
