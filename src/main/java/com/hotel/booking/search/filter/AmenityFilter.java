package com.hotel.booking.search.filter;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import org.springframework.stereotype.Component;

@Component
public class AmenityFilter implements SearchFilter {

    @Override
    public boolean isApplicable(SearchCriteria criteria) {
        return criteria.getRequiredAmenities() != null && !criteria.getRequiredAmenities().isEmpty();
    }

    @Override
    public boolean test(Property property, SearchCriteria criteria) {
        return property.getAmenities().containsAll(criteria.getRequiredAmenities());
    }
}
