package com.hotel.booking.controller;

import com.hotel.booking.domain.enums.Amenity;
import com.hotel.booking.dto.response.PropertyResponse;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.service.SearchService;
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

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

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
