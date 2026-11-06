package com.hotel.booking.controller;

import com.hotel.booking.dto.request.InitiatePaymentRequest;
import com.hotel.booking.dto.response.PaymentResponse;
import com.hotel.booking.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments", description = "Process payment for a booking")
@RestController
@RequestMapping("/api/bookings/{bookingId}/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Pay for a booking", description = "Processes payment via the specified method. On success, booking transitions from PENDING to CONFIRMED.")
    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @PathVariable Long bookingId,
            @Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.ok(
                PaymentResponse.from(paymentService.processPayment(bookingId, request.method())));
    }

    @Operation(summary = "Get payment details for a booking")
    @GetMapping
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long bookingId) {
        return ResponseEntity.ok(PaymentResponse.from(paymentService.findByBookingId(bookingId)));
    }
}
