package com.hotel.booking.service.impl;

import com.hotel.booking.cancellation.CancellationPolicy;
import com.hotel.booking.cancellation.CancellationPolicyResolver;
import com.hotel.booking.cancellation.RefundResult;
import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.entity.Payment;
import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.enums.BookingStatus;
import com.hotel.booking.domain.enums.PaymentStatus;
import com.hotel.booking.domain.valueobject.DateRange;
import com.hotel.booking.dto.request.BookingRequest;
import com.hotel.booking.exception.InventoryUnavailableException;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.payment.GatewayResponse;
import com.hotel.booking.payment.PaymentGatewayResolver;
import com.hotel.booking.pricing.PricingStrategyResolver;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.PaymentRepository;
import com.hotel.booking.repository.RoomRepository;
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
    private final PaymentRepository paymentRepository;
    private final CancellationPolicyResolver cancellationPolicyResolver;
    private final PaymentGatewayResolver paymentGatewayResolver;
    private final PricingStrategyResolver pricingStrategyResolver;

    @Override
    @Transactional
    public synchronized Booking createBooking(BookingRequest request) {
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

        return bookingRepository.save(booking);
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
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking checkOut(Long bookingId) {
        Booking booking = findById(bookingId);
        booking.transitionTo(BookingStatus.CHECKED_OUT);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking cancel(Long bookingId) {
        Booking booking = findById(bookingId);
        booking.transitionTo(BookingStatus.CANCELLED);

        paymentRepository.findByBookingId(bookingId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                CancellationPolicy policy = cancellationPolicyResolver.resolve(booking);
                RefundResult refund = policy.calculateRefund(booking);

                if (refund.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
                    GatewayResponse gatewayResponse = paymentGatewayResolver
                            .resolve(payment.getMethod())
                            .refund(payment.getTransactionId(), refund.getDescription());

                    payment.setStatus(refund.getRefundAmount().compareTo(booking.getTotalPrice()) == 0
                            ? PaymentStatus.REFUNDED : PaymentStatus.PARTIAL_REFUND);
                    paymentRepository.save(payment);
                }
            }
        });

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByGuestEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }
}
