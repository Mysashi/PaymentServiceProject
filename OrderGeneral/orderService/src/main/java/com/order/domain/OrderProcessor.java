package com.order.domain;

import com.common_libs.api.order.CreateOrderRequestDto;
import com.common_libs.api.order.OrderStatus;
import com.common_libs.api.payment.CreatePaymentRequestDto;
import com.common_libs.api.payment.CreatePaymentResponseDto;
import com.common_libs.api.payment.PaymentStatus;
import com.common_libs.kafka.DeliveryAssignedEvent;
import com.common_libs.kafka.OrderPaidEvent;
import com.order.domain.db.OrderEntity;
import com.order.domain.db.OrderEntityMapper;
import com.order.domain.db.OrderItemEntity;
import com.order.domain.db.OrderJpaRepository;
import com.order.external.PaymentHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository orderEntityRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    public OrderEntity create(CreateOrderRequestDto request) {
        var entity = orderEntityMapper.toEntity(request);
        calculateOrder(entity);
        entity.setOrderStatus(OrderStatus.PAYMENT_PENDING);
        return orderEntityRepository.save(entity);
    }

    public OrderEntity getOrderOrThrow(Long id) {
        Optional<OrderEntity> orderItemEntityOptional = orderEntityRepository.findById(id);
        return orderItemEntityOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    private void calculateOrder(OrderEntity entity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemEntity orderItem : entity.getItems()) {
            double randomPrice = ThreadLocalRandom.current().nextDouble(1000, 10000);
            orderItem.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));
            totalPrice = totalPrice.add(orderItem.getPriceAtPurchase()).multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        }
        entity.setTotalAmount(totalPrice);
    }

    public OrderEntity processPayment(Long id, OrderPaymentRequest request) {
        var entity = getOrderOrThrow(id);
        if (entity.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new RuntimeException("OrderEntity is not pending payment");
        }
        var response = paymentHttpClient.createPayment(CreatePaymentRequestDto
                .builder()
                .orderId(id)
                .paymentMethod(request.paymentMethod())
                .amount(entity.getTotalAmount()).build());
        var status = response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCCESS)
                ? OrderStatus.PAID
                : OrderStatus.FAIL;
        entity.setOrderStatus(status);
        sendOrderPaidEvent(entity, response);
        return orderEntityRepository.save(entity);
    }

    private void sendOrderPaidEvent(OrderEntity entity, CreatePaymentResponseDto request) {
        kafkaTemplate.send(orderPaidTopic, entity.getId(), OrderPaidEvent.builder()
                .paymentMethod(request.paymentMethod())
                .amount(entity.getTotalAmount())
                .orderId(entity.getId())
                .build())
                .thenAccept(res -> log.info("OrderPaidEvent was sent, entity id {}", entity.getId()));
    }

    public void processDeliveryAssignedEvent(DeliveryAssignedEvent event) {
        var order = getOrderOrThrow(event.orderId());

        if (!isOrderInCorrectState(event, order)) return;

        order.setOrderStatus(OrderStatus.DELIVERY_ASSIGNED);
        order.setCourierName(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        orderEntityRepository.save(order);
        log.info("Delivery assigned for orderId={}", event.orderId());
    }

    private static boolean isOrderInCorrectState(DeliveryAssignedEvent event, OrderEntity order) {
        if (order.getOrderStatus() == OrderStatus.DELIVERY_ASSIGNED) {
            log.info("OrderId={} have already assigned delivery", event.orderId());
            return false;
        }
        else if (order.getOrderStatus() != OrderStatus.PAID) {
            log.error("OrderId={} was not paid, delivery assign cancelled", event.orderId());
            return false;
        }
        return true;
    }
}
