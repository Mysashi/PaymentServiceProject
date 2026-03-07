package com.order.kafka;

import com.common_libs.kafka.DeliveryAssignedEvent;
import com.order.domain.OrderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class DeliveryAssignedKafkaConsumer {

    private final OrderProcessor orderProcessor;

    @KafkaListener(
            topics = "${delivery-assigned-topic}",
            containerFactory ="deliveryAssignedEventListenerFactory"
    )
    public void listen(DeliveryAssignedEvent event) {
        log.info("delivery assigned for orderId={}", event.orderId());
        orderProcessor.processDeliveryAssignedEvent(event);
    }
}
