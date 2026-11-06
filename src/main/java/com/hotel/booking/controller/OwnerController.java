package com.hotel.booking.controller;

import com.hotel.booking.dto.request.CreateOwnerRequest;
import com.hotel.booking.dto.response.OwnerResponse;
import com.hotel.booking.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Owners", description = "Register and manage property owners")
@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @Operation(summary = "Register a new owner")
    @PostMapping
    public ResponseEntity<OwnerResponse> register(@Valid @RequestBody CreateOwnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OwnerResponse.from(ownerService.register(request)));
    }

    @Operation(summary = "Get owner by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(OwnerResponse.from(ownerService.findById(id)));
    }

    @Operation(summary = "List all owners")
    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAll() {
        return ResponseEntity.ok(ownerService.findAll().stream().map(OwnerResponse::from).toList());
    }
}
