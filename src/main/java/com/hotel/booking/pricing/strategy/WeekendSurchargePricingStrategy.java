package com.hotel.booking.pricing.strategy;

import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.enums.RoomType;
import com.hotel.booking.domain.valueobject.DateRange;
import com.hotel.booking.pricing.PricingStrategy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@Order(1)
public class WeekendSurchargePricingStrategy implements PricingStrategy {

    private static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.25");

    @Override
    public BigDecimal calculateTotalPrice(Room room, DateRange dateRange) {
        BigDecimal total = BigDecimal.ZERO;
        LocalDate current = dateRange.getCheckInDate();
        while (current.isBefore(dateRange.getCheckOutDate())) {
            BigDecimal nightPrice = isWeekend(current)
                    ? room.getPricePerNight().multiply(WEEKEND_MULTIPLIER)
                    : room.getPricePerNight();
            total = total.add(nightPrice);
            current = current.plusDays(1);
        }
        return total;
    }

    @Override
    public boolean isApplicable(Room room, DateRange dateRange) {
        return room.getRoomType() == RoomType.SUITE || room.getRoomType() == RoomType.PENTHOUSE;
    }

    @Override
    public String strategyName() {
        return "WEEKEND_SURCHARGE";
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
