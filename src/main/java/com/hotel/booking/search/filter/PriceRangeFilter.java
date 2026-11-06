package com.hotel.booking.search.filter;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceRangeFilter implements SearchFilter {

    @Override
    public boolean isApplicable(SearchCriteria criteria) {
        return criteria.getMinPricePerNight() != null || criteria.getMaxPricePerNight() != null;
    }

    @Override
    public boolean test(Property property, SearchCriteria criteria) {
        return property.getRooms().stream().anyMatch(room -> matchesPrice(room, criteria));
    }

    private boolean matchesPrice(Room room, SearchCriteria criteria) {
        BigDecimal price = room.getPricePerNight();
        boolean aboveMin = criteria.getMinPricePerNight() == null
                || price.compareTo(criteria.getMinPricePerNight()) >= 0;
        boolean belowMax = criteria.getMaxPricePerNight() == null
                || price.compareTo(criteria.getMaxPricePerNight()) <= 0;
        return aboveMin && belowMax;
    }
}
