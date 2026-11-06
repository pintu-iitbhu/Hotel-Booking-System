package com.hotel.booking.repository;

import com.hotel.booking.domain.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByLocationCityIgnoreCase(String city);
    List<Property> findByOwnerId(Long ownerId);
}
