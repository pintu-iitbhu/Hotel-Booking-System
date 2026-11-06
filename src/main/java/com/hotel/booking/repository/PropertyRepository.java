package com.hotel.booking.repository;

import com.hotel.booking.domain.entity.Property;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @EntityGraph(attributePaths = {"owner", "rooms"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Property> findAll();

    @EntityGraph(attributePaths = {"owner", "rooms"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Property> findById(Long id);

    @EntityGraph(attributePaths = {"owner", "rooms"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Property> findByLocationCityIgnoreCase(String city);

    @EntityGraph(attributePaths = {"owner", "rooms"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Property> findByOwnerId(Long ownerId);
}
