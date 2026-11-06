package com.hotel.booking.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DateRange {

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    public void validate() {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
    }

    public long nights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    public boolean overlaps(DateRange other) {
        return checkInDate.isBefore(other.checkOutDate)
                && checkOutDate.isAfter(other.checkInDate);
    }
}
