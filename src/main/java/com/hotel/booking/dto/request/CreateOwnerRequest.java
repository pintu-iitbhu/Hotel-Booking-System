package com.hotel.booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateOwnerRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        String phone
) {}
