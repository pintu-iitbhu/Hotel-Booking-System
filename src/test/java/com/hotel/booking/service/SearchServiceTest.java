package com.hotel.booking.service;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.domain.enums.Amenity;
import com.hotel.booking.domain.valueobject.Location;
import com.hotel.booking.repository.PropertyRepository;
import com.hotel.booking.search.SearchCriteria;
import com.hotel.booking.search.SearchFilter;
import com.hotel.booking.search.filter.AmenityFilter;
import com.hotel.booking.search.filter.CityFilter;
import com.hotel.booking.search.filter.StarRatingFilter;
import com.hotel.booking.service.impl.SearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock private PropertyRepository propertyRepository;

    private SearchServiceImpl searchService;
    private Property mumbaiProperty;
    private Property delhiProperty;

    @BeforeEach
    void setUp() {
        mumbaiProperty = buildProperty(1L, "Sea View Resort", "Mumbai", 5, Set.of(Amenity.POOL, Amenity.WIFI));
        delhiProperty  = buildProperty(2L, "Capital Inn",    "Delhi",  3, Set.of(Amenity.WIFI));

        List<SearchFilter> filters = List.of(new CityFilter(), new StarRatingFilter(), new AmenityFilter());
        searchService = new SearchServiceImpl(propertyRepository, filters);
    }

    @Test
    void search_byCityMumbai_returnsOnlyMumbaiProperties() {
        when(propertyRepository.findAll()).thenReturn(List.of(mumbaiProperty, delhiProperty));

        SearchCriteria criteria = SearchCriteria.builder().city("Mumbai").build();
        List<Property> results = searchService.search(criteria);

        assertThat(results).containsExactly(mumbaiProperty);
    }

    @Test
    void search_byMinStarRating4_filtersOutLowRated() {
        when(propertyRepository.findAll()).thenReturn(List.of(mumbaiProperty, delhiProperty));

        SearchCriteria criteria = SearchCriteria.builder().minStarRating(4).build();
        List<Property> results = searchService.search(criteria);

        assertThat(results).containsExactly(mumbaiProperty);
    }

    @Test
    void search_byAmenityPool_returnsOnlyPoolProperties() {
        when(propertyRepository.findAll()).thenReturn(List.of(mumbaiProperty, delhiProperty));

        SearchCriteria criteria = SearchCriteria.builder()
                .requiredAmenities(Set.of(Amenity.POOL))
                .build();
        List<Property> results = searchService.search(criteria);

        assertThat(results).containsExactly(mumbaiProperty);
    }

    @Test
    void search_noCriteria_returnsAll() {
        when(propertyRepository.findAll()).thenReturn(List.of(mumbaiProperty, delhiProperty));

        List<Property> results = searchService.search(SearchCriteria.builder().build());

        assertThat(results).hasSize(2);
    }

    private Property buildProperty(Long id, String name, String city, int stars, Set<Amenity> amenities) {
        Property p = new Property();
        p.setId(id);
        p.setName(name);
        p.setStarRating(stars);
        p.setLocation(new Location("123 Main St", "Central", city, "India"));
        p.setAmenities(amenities);
        return p;
    }
}
