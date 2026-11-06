package com.hotel.booking.repository;

import com.hotel.booking.domain.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @EntityGraph(attributePaths = {"booking"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Payment> findByBookingId(Long bookingId);
}
