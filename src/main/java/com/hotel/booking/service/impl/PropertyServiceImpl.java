package com.hotel.booking.service.impl;

import com.hotel.booking.domain.entity.Owner;
import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.valueobject.Location;
import com.hotel.booking.dto.request.CreatePropertyRequest;
import com.hotel.booking.dto.request.CreateRoomRequest;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.repository.OwnerRepository;
import com.hotel.booking.repository.PropertyRepository;
import com.hotel.booking.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final OwnerRepository ownerRepository;

    @Override
    @Transactional
    public Property addProperty(Long ownerId, CreatePropertyRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", ownerId));

        Property property = new Property();
        property.setName(request.name());
        property.setDescription(request.description());
        property.setStarRating(request.starRating());
        property.setLocation(new Location(
                request.address(), request.locality(), request.city(), request.country()));
        property.setAmenities(request.amenities() != null ? request.amenities() : new HashSet<>());

        for (CreateRoomRequest roomReq : request.rooms()) {
            Room room = new Room();
            room.setRoomNumber(roomReq.roomNumber());
            room.setRoomType(roomReq.roomType());
            room.setPricePerNight(roomReq.pricePerNight());
            room.setCapacity(roomReq.capacity());
            room.setAvailable(true);
            property.addRoom(room);
        }

        owner.addProperty(property);
        return propertyRepository.save(property);
    }

    @Override
    @Transactional(readOnly = true)
    public Property findById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> findByOwner(Long ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }
}
