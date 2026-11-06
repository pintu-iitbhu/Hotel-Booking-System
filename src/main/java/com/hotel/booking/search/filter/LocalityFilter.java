package com.hotel.booking.search.filter;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import org.springframework.stereotype.Component;

@Component
public class LocalityFilter implements SearchFilter {

    @Override
    public boolean isApplicable(SearchCriteria criteria) {
        return criteria.getLocality() != null && !criteria.getLocality().isBlank();
    }

    @Override
    public boolean test(Property property, SearchCriteria criteria) {
        String locality = property.getLocation().getLocality();
        return locality != null && locality.equalsIgnoreCase(criteria.getLocality());
    }
}
