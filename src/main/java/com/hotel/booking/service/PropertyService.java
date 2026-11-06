package com.hotel.booking.service;

import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.dto.request.CreatePropertyRequest;

import java.util.List;

public interface PropertyService {
    Property addProperty(Long ownerId, CreatePropertyRequest request);
    Property findById(Long id);
    List<Property> findByOwner(Long ownerId);
}
