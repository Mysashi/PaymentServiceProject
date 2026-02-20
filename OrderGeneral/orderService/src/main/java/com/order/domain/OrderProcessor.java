package com.order.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository orderEntityRepository;

    public OrderEntity create(OrderEntity orderEntity) {
        return orderEntityRepository.save(orderEntity);
    }

    public OrderEntity getOrderOrThrow(Long id) {
        Optional<OrderEntity> orderItemEntityOptional = orderEntityRepository.findById(id);
        return orderItemEntityOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }
}
