package com.hotel.booking.dto.response;

import com.hotel.booking.domain.entity.Payment;
import com.hotel.booking.domain.enums.PaymentMethod;
import com.hotel.booking.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long bookingId,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        String transactionId,
        String failureReason,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }
}
