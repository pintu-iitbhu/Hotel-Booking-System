package com.hotel.booking.service.impl;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.repository.PropertyRepository;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import com.hotel.booking.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final PropertyRepository propertyRepository;
    private final List<SearchFilter> filters;

    @Override
    @Transactional(readOnly = true)
    public List<Property> search(SearchCriteria criteria) {
        return propertyRepository.findAll().stream()
                .filter(property -> filters.stream()
                        .filter(f -> f.isApplicable(criteria))
                        .allMatch(f -> f.test(property, criteria)))
                .toList();
    }
}
