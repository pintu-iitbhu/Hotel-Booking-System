package com.hotel.booking.service.impl;

import com.hotel.booking.domain.entity.Owner;
import com.hotel.booking.dto.request.CreateOwnerRequest;
import com.hotel.booking.exception.BadRequestException;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.repository.OwnerRepository;
import com.hotel.booking.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    @Override
    @Transactional
    public Owner register(CreateOwnerRequest request) {
        if (ownerRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Owner already registered with email: " + request.email());
        }
        Owner owner = new Owner();
        owner.setName(request.name());
        owner.setEmail(request.email());
        owner.setPhone(request.phone());
        return ownerRepository.save(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Owner findById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }
}
