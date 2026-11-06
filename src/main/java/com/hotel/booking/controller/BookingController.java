package com.hotel.booking.controller;

import com.hotel.booking.dto.request.BookingRequest;
import com.hotel.booking.dto.response.BookingResponse;
import com.hotel.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bookings", description = "Create and manage the booking lifecycle")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Create a booking", description = "Books a room for the given dates. Starts in PENDING state until payment is made.")
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BookingResponse.from(bookingService.createBooking(request)));
    }

    @Operation(summary = "Get booking by ID")
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BookingResponse.from(bookingService.findById(id)));
    }

    @Operation(summary = "Get all bookings for a guest by email")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getByGuest(@RequestParam String email) {
        return ResponseEntity.ok(
                bookingService.findByGuestEmail(email).stream().map(BookingResponse::from).toList());
    }

    @Operation(summary = "Check in", description = "Transitions booking from CONFIRMED to CHECKED_IN.")
    @PutMapping("/{id}/check-in")
    public ResponseEntity<BookingResponse> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(BookingResponse.from(bookingService.checkIn(id)));
    }

    @Operation(summary = "Check out", description = "Transitions booking from CHECKED_IN to CHECKED_OUT.")
    @PutMapping("/{id}/check-out")
    public ResponseEntity<BookingResponse> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(BookingResponse.from(bookingService.checkOut(id)));
    }

    @Operation(summary = "Cancel a booking", description = "Cancels the booking and triggers a refund if the booking was paid. Inventory is released immediately.")
    @DeleteMapping("/{id}")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(BookingResponse.from(bookingService.cancel(id)));
    }
}
