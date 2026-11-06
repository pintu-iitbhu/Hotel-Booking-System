package com.hotel.booking.payment;

import com.hotel.booking.domain.enums.PaymentMethod;
import com.hotel.booking.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentGatewayResolver {

    private final List<PaymentGateway> gateways;

    public PaymentGateway resolve(PaymentMethod method) {
        return gateways.stream()
                .filter(g -> g.supports(method))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No payment gateway found for method: " + method));
    }
}
