package com.hotel.booking.search;

import com.hotel.booking.domain.enums.Amenity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
public class SearchCriteria {
    private String city;
    private String locality;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numGuests;
    private BigDecimal minPricePerNight;
    private BigDecimal maxPricePerNight;
    private Set<Amenity> requiredAmenities;
    private Integer minStarRating;
}
