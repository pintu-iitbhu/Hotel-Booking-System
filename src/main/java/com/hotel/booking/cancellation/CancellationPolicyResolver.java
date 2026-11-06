package com.hotel.booking.cancellation;

import com.hotel.booking.cancellation.policy.FreeCancellationPolicy;
import com.hotel.booking.cancellation.policy.NonRefundablePolicy;
import com.hotel.booking.cancellation.policy.PartialRefundPolicy;
import com.hotel.booking.domain.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class CancellationPolicyResolver {

    private final FreeCancellationPolicy freeCancellationPolicy;
    private final PartialRefundPolicy partialRefundPolicy;
    private final NonRefundablePolicy nonRefundablePolicy;

    public CancellationPolicy resolve(Booking booking) {
        long daysUntilCheckIn = ChronoUnit.DAYS.between(
                LocalDate.now(), booking.getDateRange().getCheckInDate());

        if (daysUntilCheckIn >= 7) return freeCancellationPolicy;
        if (daysUntilCheckIn >= 2) return partialRefundPolicy;
        return nonRefundablePolicy;
    }
}
