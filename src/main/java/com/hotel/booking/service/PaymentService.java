package com.hotel.booking.service;

import com.hotel.booking.domain.entity.Payment;
import com.hotel.booking.domain.enums.PaymentMethod;

public interface PaymentService {
    Payment processPayment(Long bookingId, PaymentMethod method);
    void processRefundIfPaid(Long bookingId);
    Payment findByBookingId(Long bookingId);
}
