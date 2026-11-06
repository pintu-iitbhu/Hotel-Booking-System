package com.hotel.booking.pricing;

import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.valueobject.DateRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PricingStrategyResolver {

    private final List<PricingStrategy> strategies;

    public BigDecimal resolve(Room room, DateRange dateRange) {
        return strategies.stream()
                .filter(s -> s.isApplicable(room, dateRange))
                .findFirst()
                .map(s -> s.calculateTotalPrice(room, dateRange))
                .orElse(room.getPricePerNight().multiply(BigDecimal.valueOf(dateRange.nights())));
    }
}
