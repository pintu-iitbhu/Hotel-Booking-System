package com.hotel.booking.payment.gateway;

import com.hotel.booking.domain.enums.PaymentMethod;
import com.hotel.booking.payment.GatewayRequest;
import com.hotel.booking.payment.GatewayResponse;
import com.hotel.booking.payment.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpiPaymentGateway implements PaymentGateway {

    @Override
    public GatewayResponse process(GatewayRequest request) {
        return GatewayResponse.builder()
                .success(true)
                .transactionId("UPI-" + UUID.randomUUID())
                .build();
    }

    @Override
    public GatewayResponse refund(String transactionId, String reason) {
        return GatewayResponse.builder()
                .success(true)
                .transactionId("REFUND-UPI-" + UUID.randomUUID())
                .build();
    }

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.UPI;
    }
}
