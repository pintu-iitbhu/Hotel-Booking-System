package com.hotel.booking.cancellation;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RefundResult {
    private BigDecimal refundAmount;
    private String policyApplied;
    private String description;
}
