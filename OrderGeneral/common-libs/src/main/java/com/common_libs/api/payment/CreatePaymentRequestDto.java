package com.common_libs.api.payment;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRequestDto(
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) { }

