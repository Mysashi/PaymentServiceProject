package com.payment.api;

import com.common_libs.api.payment.CreatePaymentRequestDto;
import com.common_libs.api.payment.CreatePaymentResponseDto;
import com.payment.domain.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentProcessor paymentProcessor;

    @PostMapping
    public CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto request) {
        log.info("Received payment with request={}", request);
        return paymentProcessor.createPayment(request);
    }
}
