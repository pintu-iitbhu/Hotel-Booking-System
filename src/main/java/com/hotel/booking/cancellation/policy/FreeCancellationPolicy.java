package com.hotel.booking.cancellation.policy;

import com.hotel.booking.cancellation.CancellationPolicy;
import com.hotel.booking.cancellation.RefundResult;
import com.hotel.booking.domain.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class FreeCancellationPolicy implements CancellationPolicy {

    @Override
    public RefundResult calculateRefund(Booking booking) {
        return RefundResult.builder()
                .refundAmount(booking.getTotalPrice())
                .policyApplied(policyName())
                .description("Full refund — cancelled 7+ days before check-in")
                .build();
    }

    @Override
    public String policyName() {
        return "FREE_CANCELLATION";
    }
}
