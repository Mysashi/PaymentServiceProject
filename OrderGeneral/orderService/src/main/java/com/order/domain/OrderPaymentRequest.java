package com.order.domain;

import com.common_libs.api.payment.PaymentMethod;

public record OrderPaymentRequest(PaymentMethod paymentMethod) {
}
