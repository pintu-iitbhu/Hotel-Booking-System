package com.hotel.booking.pricing;

import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.valueobject.DateRange;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculateTotalPrice(Room room, DateRange dateRange);

    boolean isApplicable(Room room, DateRange dateRange);

    String strategyName();
}
