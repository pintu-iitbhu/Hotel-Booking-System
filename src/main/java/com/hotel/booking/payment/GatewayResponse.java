package com.hotel.booking.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GatewayResponse {
    private boolean success;
    private String transactionId;
    private String failureReason;
}
