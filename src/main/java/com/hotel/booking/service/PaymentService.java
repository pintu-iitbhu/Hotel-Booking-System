package com.hotel.booking.service;

import com.hotel.booking.domain.entity.Payment;
import com.hotel.booking.domain.enums.PaymentMethod;

public interface PaymentService {
    Payment processPayment(Long bookingId, PaymentMethod method);
    Payment findByBookingId(Long bookingId);
}
