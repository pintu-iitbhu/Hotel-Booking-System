package com.hotel.booking.service;

import com.hotel.booking.cancellation.CancellationPolicyResolver;
import com.hotel.booking.cancellation.RefundResult;
import com.hotel.booking.cancellation.policy.FreeCancellationPolicy;
import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.enums.BookingStatus;
import com.hotel.booking.domain.enums.RoomType;
import com.hotel.booking.domain.valueobject.DateRange;
import com.hotel.booking.dto.request.BookingRequest;
import com.hotel.booking.exception.InventoryUnavailableException;
import com.hotel.booking.exception.InvalidStateTransitionException;
import com.hotel.booking.payment.PaymentGatewayResolver;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.PaymentRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private CancellationPolicyResolver cancellationPolicyResolver;
    @Mock private PaymentGatewayResolver paymentGatewayResolver;

    @InjectMocks private BookingServiceImpl bookingService;

    private Room room;
    private Property property;
    private BookingRequest validRequest;

    @BeforeEach
    void setUp() {
        property = new Property();
        property.setId(1L);
        property.setName("Grand Hotel");

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DOUBLE);
        room.setPricePerNight(new BigDecimal("2000"));
        room.setCapacity(2);
        room.setAvailable(true);
        room.setProperty(property);

        validRequest = new BookingRequest(
                1L, RoomType.DOUBLE,
                "Alice Smith", "alice@example.com", "9876543210",
                2,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                null
        );
    }

    @Test
    void createBooking_success() {
        when(roomRepository.findAvailableRooms(any(), any(), anyInt(), any(), any()))
                .thenReturn(List.of(room));

        Booking saved = new Booking();
        saved.setId(1L);
        saved.setRoom(room);
        saved.setDateRange(new DateRange(validRequest.checkInDate(), validRequest.checkOutDate()));
        saved.setTotalPrice(new BigDecimal("4000"));
        saved.setStatus(BookingStatus.PENDING);
        saved.setGuestName(validRequest.guestName());
        saved.setGuestEmail(validRequest.guestEmail());
        saved.setNumGuests(validRequest.numGuests());

        when(bookingRepository.save(any())).thenReturn(saved);

        Booking result = bookingService.createBooking(validRequest);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(result.getTotalPrice()).isEqualByComparingTo("4000");
        assertThat(result.getRoom()).isEqualTo(room);
    }

    @Test
    void createBooking_noRoomsAvailable_throwsInventoryUnavailableException() {
        when(roomRepository.findAvailableRooms(any(), any(), anyInt(), any(), any()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> bookingService.createBooking(validRequest))
                .isInstanceOf(InventoryUnavailableException.class)
                .hasMessageContaining("No DOUBLE rooms available");
    }

    @Test
    void bookingStateTransition_pendingToConfirmed_valid() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);

        assertThatCode(() -> booking.transitionTo(BookingStatus.CONFIRMED))
                .doesNotThrowAnyException();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void bookingStateTransition_checkedOutToCancelled_invalid() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CHECKED_OUT);

        assertThatThrownBy(() -> booking.transitionTo(BookingStatus.CANCELLED))
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasMessageContaining("CHECKED_OUT");
    }

    @Test
    void bookingStateTransition_confirmedToCheckedIn_valid() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CONFIRMED);

        booking.transitionTo(BookingStatus.CHECKED_IN);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CHECKED_IN);
    }

    @Test
    void cancel_withNoPayment_cancelsBooking() {
        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(new BigDecimal("4000"));
        booking.setRoom(room);
        DateRange dateRange = new DateRange(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));
        booking.setDateRange(dateRange);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(10L)).thenReturn(Optional.empty());
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking cancelled = bookingService.cancel(10L);

        assertThat(cancelled.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void totalPrice_calculatedCorrectly_basedOnNights() {
        when(roomRepository.findAvailableRooms(any(), any(), anyInt(), any(), any()))
                .thenReturn(List.of(room));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(validRequest);

        long expectedNights = 2;
        BigDecimal expected = new BigDecimal("2000").multiply(BigDecimal.valueOf(expectedNights));
        assertThat(result.getTotalPrice()).isEqualByComparingTo(expected);
    }
}
