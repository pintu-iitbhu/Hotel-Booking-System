package com.hotel.booking.controller;

import com.hotel.booking.dto.request.BookingRequest;
import com.hotel.booking.dto.response.BookingResponse;
import com.hotel.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BookingResponse.from(bookingService.createBooking(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BookingResponse.from(bookingService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getByGuest(@RequestParam String email) {
        return ResponseEntity.ok(
                bookingService.findByGuestEmail(email).stream().map(BookingResponse::from).toList());
    }

    @PutMapping("/{id}/check-in")
    public ResponseEntity<BookingResponse> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(BookingResponse.from(bookingService.checkIn(id)));
    }

    @PutMapping("/{id}/check-out")
    public ResponseEntity<BookingResponse> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(BookingResponse.from(bookingService.checkOut(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(BookingResponse.from(bookingService.cancel(id)));
    }
}
