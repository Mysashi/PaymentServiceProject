package com.payment.domain;

import com.payment.api.CreatePaymentRequestDto;
import com.payment.api.CreatePaymentResponseDto;
import com.payment.domain.db.PaymentEntity;
import com.payment.domain.db.PaymentEntityMapper;
import com.payment.domain.db.PaymentJpaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentProcessor {


    private final PaymentJpaRepository paymentJpaRepository;

    private final PaymentEntityMapper mapper;

    public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto request) {
        var entity = checkPaymentIfExisting(request);
        applyPaymentMethod(entity);
        var saved = paymentJpaRepository.save(entity);
        return mapper.toResponseDto(saved);
    }

    private PaymentEntity checkPaymentIfExisting(CreatePaymentRequestDto request) {
        var found = paymentJpaRepository.findByOrderId(request.orderId());
        if (found.isPresent()) {
            log.info("Payment already existing with id={}", request.orderId());
            return found.get();
        }
        return mapper.toEntity(request);
    }

    private void applyPaymentMethod(PaymentEntity entity) {
        var status = entity.getPaymentMethod() == PaymentMethod.YANDEX_SPLIT ? PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCCESS;
        entity.setPaymentStatus(status);
    }

}
