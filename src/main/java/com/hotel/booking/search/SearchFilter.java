package com.hotel.booking.search;

import com.hotel.booking.domain.entity.Property;

public interface SearchFilter {

    boolean test(Property property, SearchCriteria criteria);

    default boolean isApplicable(SearchCriteria criteria) {
        return true;
    }
}
