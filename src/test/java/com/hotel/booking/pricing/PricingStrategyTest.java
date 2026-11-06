package com.hotel.booking.pricing;

import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.enums.RoomType;
import com.hotel.booking.domain.valueobject.DateRange;
import com.hotel.booking.pricing.strategy.FlatNightlyPricingStrategy;
import com.hotel.booking.pricing.strategy.WeekendSurchargePricingStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PricingStrategyTest {

    private final FlatNightlyPricingStrategy flatStrategy       = new FlatNightlyPricingStrategy();
    private final WeekendSurchargePricingStrategy weekendStrategy = new WeekendSurchargePricingStrategy();

    @Test
    void flatNightly_calculates_priceTimesNights() {
        Room room = roomWith(RoomType.DOUBLE, new BigDecimal("1000"));
        DateRange range = new DateRange(LocalDate.of(2027, 1, 6), LocalDate.of(2027, 1, 9));

        BigDecimal total = flatStrategy.calculateTotalPrice(room, range);

        assertThat(total).isEqualByComparingTo("3000");
    }

    @Test
    void flatNightly_isApplicable_forAllRoomTypes() {
        Room room = roomWith(RoomType.SUITE, new BigDecimal("5000"));
        DateRange range = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        assertThat(flatStrategy.isApplicable(room, range)).isTrue();
    }

    @Test
    void weekendSurcharge_onlyApplicable_forSuiteAndPenthouse() {
        assertThat(weekendStrategy.isApplicable(roomWith(RoomType.SUITE, BigDecimal.TEN), null)).isTrue();
        assertThat(weekendStrategy.isApplicable(roomWith(RoomType.PENTHOUSE, BigDecimal.TEN), null)).isTrue();
        assertThat(weekendStrategy.isApplicable(roomWith(RoomType.DOUBLE, BigDecimal.TEN), null)).isFalse();
    }

    @Test
    void weekendSurcharge_appliesSurcharge_onSaturdayAndSunday() {
        Room room = roomWith(RoomType.SUITE, new BigDecimal("1000"));
        LocalDate friday   = LocalDate.of(2027, 1, 8);
        LocalDate monday   = LocalDate.of(2027, 1, 11);
        DateRange fri2mon  = new DateRange(friday, monday);

        BigDecimal total = weekendStrategy.calculateTotalPrice(room, fri2mon);

        BigDecimal expected = new BigDecimal("1000")
                .add(new BigDecimal("1000").multiply(new BigDecimal("1.25")))
                .add(new BigDecimal("1000").multiply(new BigDecimal("1.25")));
        assertThat(total).isEqualByComparingTo(expected);
    }

    @Test
    void resolver_picks_weekendStrategy_forSuiteWhenApplicable() {
        PricingStrategyResolver resolver = new PricingStrategyResolver(
                java.util.List.of(weekendStrategy, flatStrategy));

        Room suite = roomWith(RoomType.SUITE, new BigDecimal("2000"));
        // Jan 8 (Fri) → Jan 11 (Mon): includes Sat + Sun, so surcharge applies
        DateRange range = new DateRange(LocalDate.of(2027, 1, 8), LocalDate.of(2027, 1, 11));

        BigDecimal total = resolver.resolve(suite, range);

        assertThat(total).isNotEqualByComparingTo(
                flatStrategy.calculateTotalPrice(suite, range));
    }

    private Room roomWith(RoomType type, BigDecimal price) {
        Room room = new Room();
        room.setRoomType(type);
        room.setPricePerNight(price);
        return room;
    }
}
