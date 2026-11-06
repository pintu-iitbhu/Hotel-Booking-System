package com.hotel.booking.cancellation;

import com.hotel.booking.domain.entity.Booking;

public interface CancellationPolicy {

    RefundResult calculateRefund(Booking booking);

    String policyName();
}
