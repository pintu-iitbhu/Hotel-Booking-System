package com.hotel.booking.service;

import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.entity.Property;
import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.enums.BookingStatus;
import com.hotel.booking.domain.enums.RoomType;
import com.hotel.booking.domain.valueobject.DateRange;
import com.hotel.booking.dto.request.BookingRequest;
import com.hotel.booking.exception.InventoryUnavailableException;
import com.hotel.booking.exception.InvalidStateTransitionException;
import com.hotel.booking.pricing.PricingStrategyResolver;
import com.hotel.booking.repository.BookingRepository;
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
    @Mock private PaymentService paymentService;
    @Mock private PricingStrategyResolver pricingStrategyResolver;

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

        when(pricingStrategyResolver.resolve(any(), any())).thenReturn(new BigDecimal("4000"));
        when(bookingRepository.save(any())).thenReturn(saved);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(saved));

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
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking cancelled = bookingService.cancel(10L);

        assertThat(cancelled.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(paymentService).processRefundIfPaid(10L);
    }

    @Test
    void totalPrice_calculatedCorrectly_basedOnNights() {
        BigDecimal expectedTotal = new BigDecimal("4000");
        when(roomRepository.findAvailableRooms(any(), any(), anyInt(), any(), any()))
                .thenReturn(List.of(room));
        when(pricingStrategyResolver.resolve(any(), any())).thenReturn(expectedTotal);
        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setRoom(room);
        savedBooking.setDateRange(new DateRange(validRequest.checkInDate(), validRequest.checkOutDate()));
        savedBooking.setTotalPrice(expectedTotal);
        savedBooking.setStatus(BookingStatus.PENDING);
        when(bookingRepository.save(any())).thenReturn(savedBooking);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(savedBooking));

        Booking result = bookingService.createBooking(validRequest);

        assertThat(result.getTotalPrice()).isEqualByComparingTo(expectedTotal);
    }
}
