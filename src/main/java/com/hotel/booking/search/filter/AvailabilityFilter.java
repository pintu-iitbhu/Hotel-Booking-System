package com.hotel.booking.search.filter;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AvailabilityFilter implements SearchFilter {

    private final RoomRepository roomRepository;

    @Override
    public boolean isApplicable(SearchCriteria criteria) {
        return criteria.getCheckInDate() != null && criteria.getCheckOutDate() != null;
    }

    @Override
    public boolean test(Property property, SearchCriteria criteria) {
        int guests = criteria.getNumGuests() != null ? criteria.getNumGuests() : 1;
        return !roomRepository.findAvailableRoomsForProperty(
                property.getId(),
                guests,
                criteria.getCheckInDate(),
                criteria.getCheckOutDate()
        ).isEmpty();
    }
}
