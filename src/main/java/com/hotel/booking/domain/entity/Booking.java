package com.hotel.booking.domain.entity;

import com.hotel.booking.domain.enums.BookingStatus;
import com.hotel.booking.domain.valueobject.DateRange;
import com.hotel.booking.exception.InvalidStateTransitionException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String guestName;

    @Column(nullable = false)
    private String guestEmail;

    private String guestPhone;

    @Min(1)
    @Column(nullable = false)
    private Integer numGuests;

    @Embedded
    private DateRange dateRange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private String specialRequests;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void transitionTo(BookingStatus target) {
        if (!isValidTransition(this.status, target)) {
            throw new InvalidStateTransitionException(
                    "Cannot transition booking from " + this.status + " to " + target);
        }
        this.status = target;
    }

    private boolean isValidTransition(BookingStatus from, BookingStatus to) {
        return switch (from) {
            case PENDING    -> to == BookingStatus.CONFIRMED || to == BookingStatus.CANCELLED;
            case CONFIRMED  -> to == BookingStatus.CHECKED_IN || to == BookingStatus.CANCELLED;
            case CHECKED_IN -> to == BookingStatus.CHECKED_OUT;
            default         -> false;
        };
    }
}
