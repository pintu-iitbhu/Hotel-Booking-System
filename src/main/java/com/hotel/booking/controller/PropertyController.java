package com.hotel.booking.controller;

import com.hotel.booking.dto.request.CreatePropertyRequest;
import com.hotel.booking.dto.response.PropertyResponse;
import com.hotel.booking.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Properties", description = "Onboard and manage properties under an owner")
@RestController
@RequestMapping("/api/owners/{ownerId}/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @Operation(summary = "Onboard a new property under an owner")
    @PostMapping
    public ResponseEntity<PropertyResponse> addProperty(
            @PathVariable Long ownerId,
            @Valid @RequestBody CreatePropertyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PropertyResponse.from(propertyService.addProperty(ownerId, request)));
    }

    @Operation(summary = "Get property by ID")
    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> getById(
            @PathVariable Long ownerId,
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(PropertyResponse.from(propertyService.findById(propertyId)));
    }

    @Operation(summary = "List all properties belonging to an owner")
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(
                propertyService.findByOwner(ownerId).stream().map(PropertyResponse::from).toList());
    }
}
