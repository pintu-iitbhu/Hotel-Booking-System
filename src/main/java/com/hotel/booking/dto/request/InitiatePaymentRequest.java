package com.hotel.booking.dto.request;

import com.hotel.booking.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record InitiatePaymentRequest(
        @NotNull PaymentMethod method
) {}
