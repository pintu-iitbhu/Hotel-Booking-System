package com.hotel.booking.payment;

import com.hotel.booking.domain.enums.PaymentMethod;

public interface PaymentGateway {

    GatewayResponse process(GatewayRequest request);

    GatewayResponse refund(String transactionId, String reason);

    boolean supports(PaymentMethod method);
}
