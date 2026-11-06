package com.hotel.booking.cancellation;

import com.hotel.booking.cancellation.policy.FreeCancellationPolicy;
import com.hotel.booking.cancellation.policy.NonRefundablePolicy;
import com.hotel.booking.cancellation.policy.PartialRefundPolicy;
import com.hotel.booking.domain.entity.Booking;
import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.valueobject.DateRange;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CancellationPolicyTest {

    private final FreeCancellationPolicy freePolicy     = new FreeCancellationPolicy();
    private final PartialRefundPolicy    partialPolicy  = new PartialRefundPolicy();
    private final NonRefundablePolicy    noRefundPolicy = new NonRefundablePolicy();

    @Test
    void freeCancellationPolicy_returnsFullRefund() {
        Booking booking = buildBooking(new BigDecimal("5000"), LocalDate.now().plusDays(10));

        RefundResult result = freePolicy.calculateRefund(booking);

        assertThat(result.getRefundAmount()).isEqualByComparingTo("5000");
        assertThat(result.getPolicyApplied()).isEqualTo("FREE_CANCELLATION");
    }

    @Test
    void partialRefundPolicy_returnsHalfRefund() {
        Booking booking = buildBooking(new BigDecimal("5000"), LocalDate.now().plusDays(4));

        RefundResult result = partialPolicy.calculateRefund(booking);

        assertThat(result.getRefundAmount()).isEqualByComparingTo("2500.00");
        assertThat(result.getPolicyApplied()).isEqualTo("PARTIAL_REFUND");
    }

    @Test
    void nonRefundablePolicy_returnsZeroRefund() {
        Booking booking = buildBooking(new BigDecimal("5000"), LocalDate.now().plusDays(1));

        RefundResult result = noRefundPolicy.calculateRefund(booking);

        assertThat(result.getRefundAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getPolicyApplied()).isEqualTo("NON_REFUNDABLE");
    }

    @Test
    void cancellationPolicyResolver_selects_freePolicyWhen7OrMoreDaysAhead() {
        CancellationPolicyResolver resolver = new CancellationPolicyResolver(
                freePolicy, partialPolicy, noRefundPolicy);

        Booking booking = buildBooking(new BigDecimal("3000"), LocalDate.now().plusDays(8));
        CancellationPolicy resolved = resolver.resolve(booking);

        assertThat(resolved).isInstanceOf(FreeCancellationPolicy.class);
    }

    @Test
    void cancellationPolicyResolver_selects_partialPolicyWhen2To6DaysAhead() {
        CancellationPolicyResolver resolver = new CancellationPolicyResolver(
                freePolicy, partialPolicy, noRefundPolicy);

        Booking booking = buildBooking(new BigDecimal("3000"), LocalDate.now().plusDays(3));
        CancellationPolicy resolved = resolver.resolve(booking);

        assertThat(resolved).isInstanceOf(PartialRefundPolicy.class);
    }

    @Test
    void cancellationPolicyResolver_selects_nonRefundableWhenLessThan2Days() {
        CancellationPolicyResolver resolver = new CancellationPolicyResolver(
                freePolicy, partialPolicy, noRefundPolicy);

        Booking booking = buildBooking(new BigDecimal("3000"), LocalDate.now().plusDays(1));
        CancellationPolicy resolved = resolver.resolve(booking);

        assertThat(resolved).isInstanceOf(NonRefundablePolicy.class);
    }

    private Booking buildBooking(BigDecimal totalPrice, LocalDate checkIn) {
        Booking booking = new Booking();
        booking.setTotalPrice(totalPrice);
        booking.setDateRange(new DateRange(checkIn, checkIn.plusDays(2)));
        return booking;
    }
}
