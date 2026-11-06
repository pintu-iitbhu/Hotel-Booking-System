package com.hotel.booking.pricing.strategy;

import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.valueobject.DateRange;
import com.hotel.booking.pricing.PricingStrategy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(10)
public class FlatNightlyPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateTotalPrice(Room room, DateRange dateRange) {
        return room.getPricePerNight().multiply(BigDecimal.valueOf(dateRange.nights()));
    }

    @Override
    public boolean isApplicable(Room room, DateRange dateRange) {
        return true;
    }

    @Override
    public String strategyName() {
        return "FLAT_NIGHTLY";
    }
}
