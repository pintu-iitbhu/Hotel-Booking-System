package com.hotel.booking.controller;

import com.hotel.booking.dto.request.CreateOwnerRequest;
import com.hotel.booking.dto.response.OwnerResponse;
import com.hotel.booking.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping
    public ResponseEntity<OwnerResponse> register(@Valid @RequestBody CreateOwnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OwnerResponse.from(ownerService.register(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(OwnerResponse.from(ownerService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAll() {
        return ResponseEntity.ok(ownerService.findAll().stream().map(OwnerResponse::from).toList());
    }
}
