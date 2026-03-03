package com.common_libs.kafka;


import com.common_libs.api.payment.PaymentMethod;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record OrderPaidEvent(Long orderId,
                             Long paymentId,
                             BigDecimal amount,
                             PaymentMethod paymentMethod) {
}
