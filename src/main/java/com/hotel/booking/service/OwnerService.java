package com.hotel.booking.service;

import com.hotel.booking.domain.entity.Owner;
import com.hotel.booking.dto.request.CreateOwnerRequest;

import java.util.List;

public interface OwnerService {
    Owner register(CreateOwnerRequest request);
    Owner findById(Long id);
    List<Owner> findAll();
}
