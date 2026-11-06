package com.hotel.booking.search.filter;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import org.springframework.stereotype.Component;

@Component
public class StarRatingFilter implements SearchFilter {

    @Override
    public boolean isApplicable(SearchCriteria criteria) {
        return criteria.getMinStarRating() != null;
    }

    @Override
    public boolean test(Property property, SearchCriteria criteria) {
        return property.getStarRating() >= criteria.getMinStarRating();
    }
}
