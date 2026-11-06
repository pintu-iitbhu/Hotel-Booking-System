package com.hotel.booking.controller;

import com.hotel.booking.dto.request.InitiatePaymentRequest;
import com.hotel.booking.dto.response.PaymentResponse;
import com.hotel.booking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings/{bookingId}/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @PathVariable Long bookingId,
            @Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.ok(
                PaymentResponse.from(paymentService.processPayment(bookingId, request.method())));
    }

    @GetMapping
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long bookingId) {
        return ResponseEntity.ok(PaymentResponse.from(paymentService.findByBookingId(bookingId)));
    }
}
