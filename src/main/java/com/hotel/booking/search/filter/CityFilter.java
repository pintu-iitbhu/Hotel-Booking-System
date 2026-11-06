package com.hotel.booking.search.filter;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import org.springframework.stereotype.Component;

@Component
public class CityFilter implements SearchFilter {

    @Override
    public boolean isApplicable(SearchCriteria criteria) {
        return criteria.getCity() != null && !criteria.getCity().isBlank();
    }

    @Override
    public boolean test(Property property, SearchCriteria criteria) {
        return property.getLocation().getCity()
                .equalsIgnoreCase(criteria.getCity());
    }
}
