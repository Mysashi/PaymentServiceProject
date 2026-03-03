package com.payment.api;

import com.payment.domain.PaymentMethod;
import com.payment.domain.PaymentStatus;

import java.math.BigDecimal;

public record CreatePaymentResponseDto(
        Long paymentId,
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        PaymentStatus paymentStatus) {
}
