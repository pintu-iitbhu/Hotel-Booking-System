package com.hotel.booking.service.impl;

import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.enums.BookingStatus;
import com.hotel.booking.domain.valueobject.DateRange;
import com.hotel.booking.dto.request.BookingRequest;
import com.hotel.booking.exception.InventoryUnavailableException;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.pricing.PricingStrategyResolver;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentService paymentService;
    private final PricingStrategyResolver pricingStrategyResolver;

    @Override
    @Transactional
    public Booking createBooking(BookingRequest request) {
        DateRange dateRange = new DateRange(request.checkInDate(), request.checkOutDate());
        dateRange.validate();

        List<Room> availableRooms = roomRepository.findAvailableRooms(
                request.propertyId(),
                request.roomType(),
                request.numGuests(),
                dateRange.getCheckInDate(),
                dateRange.getCheckOutDate()
        );

        if (availableRooms.isEmpty()) {
            throw new InventoryUnavailableException(
                    "No " + request.roomType() + " rooms available for the requested dates");
        }

        Room room = availableRooms.get(0);
        BigDecimal totalPrice = pricingStrategyResolver.resolve(room, dateRange);

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setDateRange(dateRange);
        booking.setGuestName(request.guestName());
        booking.setGuestEmail(request.guestEmail());
        booking.setGuestPhone(request.guestPhone());
        booking.setNumGuests(request.numGuests());
        booking.setTotalPrice(totalPrice);
        booking.setSpecialRequests(request.specialRequests());
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        return reload(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    @Override
    @Transactional
    public Booking checkIn(Long bookingId) {
        Booking booking = findById(bookingId);
        booking.transitionTo(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);
        return reload(bookingId);
    }

    @Override
    @Transactional
    public Booking checkOut(Long bookingId) {
        Booking booking = findById(bookingId);
        booking.transitionTo(BookingStatus.CHECKED_OUT);
        bookingRepository.save(booking);
        return reload(bookingId);
    }

    @Override
    @Transactional
    public Booking cancel(Long bookingId) {
        Booking booking = findById(bookingId);
        booking.transitionTo(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        paymentService.processRefundIfPaid(bookingId);
        return reload(bookingId);
    }

    private Booking reload(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByGuestEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }
}
