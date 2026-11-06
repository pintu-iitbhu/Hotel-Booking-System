package com.hotel.booking.service;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.search.SearchCriteria;

import java.util.List;

public interface SearchService {
    List<Property> search(SearchCriteria criteria);
}
