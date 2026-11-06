package com.hotel.booking.cancellation.policy;

import com.hotel.booking.cancellation.CancellationPolicy;
import com.hotel.booking.cancellation.RefundResult;
import com.hotel.booking.domain.entity.Booking;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NonRefundablePolicy implements CancellationPolicy {

    @Override
    public RefundResult calculateRefund(Booking booking) {
        return RefundResult.builder()
                .refundAmount(BigDecimal.ZERO)
                .policyApplied(policyName())
                .description("No refund — cancelled less than 2 days before check-in")
                .build();
    }

    @Override
    public String policyName() {
        return "NON_REFUNDABLE";
    }
}
