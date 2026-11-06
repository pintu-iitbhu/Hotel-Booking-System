package com.hotel.booking.cancellation.policy;

import com.hotel.booking.cancellation.CancellationPolicy;
import com.hotel.booking.cancellation.RefundResult;
import com.hotel.booking.domain.entity.Booking;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PartialRefundPolicy implements CancellationPolicy {

    private static final BigDecimal REFUND_PERCENT = new BigDecimal("0.50");

    @Override
    public RefundResult calculateRefund(Booking booking) {
        BigDecimal refund = booking.getTotalPrice()
                .multiply(REFUND_PERCENT)
                .setScale(2, RoundingMode.HALF_UP);
        return RefundResult.builder()
                .refundAmount(refund)
                .policyApplied(policyName())
                .description("50% refund — cancelled 2–6 days before check-in")
                .build();
    }

    @Override
    public String policyName() {
        return "PARTIAL_REFUND";
    }
}
