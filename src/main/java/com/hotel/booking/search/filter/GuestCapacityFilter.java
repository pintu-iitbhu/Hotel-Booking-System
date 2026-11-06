package com.hotel.booking.search.filter;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import org.springframework.stereotype.Component;

@Component
public class GuestCapacityFilter implements SearchFilter {

    @Override
    public boolean isApplicable(SearchCriteria criteria) {
        return criteria.getNumGuests() != null
                && (criteria.getCheckInDate() == null || criteria.getCheckOutDate() == null);
    }

    @Override
    public boolean test(Property property, SearchCriteria criteria) {
        return property.getRooms().stream()
                .anyMatch(room -> room.getAvailable()
                        && room.getCapacity() >= criteria.getNumGuests());
    }
}
