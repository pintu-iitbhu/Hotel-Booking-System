package com.hotel.booking.payment;

import com.hotel.booking.domain.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GatewayRequest {
    private Long bookingId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String idempotencyKey;
}
