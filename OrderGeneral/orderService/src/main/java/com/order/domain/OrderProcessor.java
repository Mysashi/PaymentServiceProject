package com.order.domain;

import com.order.api.CreateOrderRequestDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository orderEntityRepository;

    private final OrderEntityMapper orderEntityMapper;


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
}
