package com.hotel.booking.controller;

import com.hotel.booking.domain.enums.Amenity;
import com.hotel.booking.dto.response.PropertyResponse;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Tag(name = "Discovery", description = "Search and browse available properties")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "Search properties",
            description = "All params are optional — pass any combination of city, locality, dates, numGuests, price range, amenities, minStarRating.")
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) Integer numGuests,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Set<Amenity> amenities,
            @RequestParam(required = false) Integer minStarRating) {

        SearchCriteria criteria = SearchCriteria.builder()
                .city(city)
                .locality(locality)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .numGuests(numGuests)
                .minPricePerNight(minPrice)
                .maxPricePerNight(maxPrice)
                .requiredAmenities(amenities)
                .minStarRating(minStarRating)
                .build();

        return ResponseEntity.ok(
                searchService.search(criteria).stream().map(PropertyResponse::from).toList());
    }
}
