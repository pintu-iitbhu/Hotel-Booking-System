package com.hotel.booking.service.impl;

import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.entity.Payment;
import com.hotel.booking.domain.enums.BookingStatus;
import com.hotel.booking.domain.enums.PaymentMethod;
import com.hotel.booking.domain.enums.PaymentStatus;
import com.hotel.booking.exception.BadRequestException;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.payment.GatewayRequest;
import com.hotel.booking.payment.GatewayResponse;
import com.hotel.booking.payment.PaymentGatewayResolver;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.PaymentRepository;
import com.hotel.booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentGatewayResolver gatewayResolver;

    @Override
    @Transactional
    public Payment processPayment(Long bookingId, PaymentMethod method) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException(
                    "Payment can only be made for PENDING bookings. Current status: " + booking.getStatus());
        }

        paymentRepository.findByBookingId(bookingId).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.SUCCESS) {
                throw new BadRequestException("Booking " + bookingId + " is already paid");
            }
        });

        GatewayRequest gatewayRequest = GatewayRequest.builder()
                .bookingId(bookingId)
                .amount(booking.getTotalPrice())
                .method(method)
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

        GatewayResponse gatewayResponse = gatewayResolver.resolve(method).process(gatewayRequest);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setMethod(method);
        payment.setTransactionId(gatewayResponse.getTransactionId());

        if (gatewayResponse.isSuccess()) {
            payment.setStatus(PaymentStatus.SUCCESS);
            booking.transitionTo(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(gatewayResponse.getFailureReason());
        }

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment findByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for booking", bookingId));
    }
}
