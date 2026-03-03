package com.order.api;

import com.common_libs.api.order.CreateOrderRequestDto;
import com.common_libs.api.order.OrderDto;
import com.order.domain.OrderPaymentRequest;
import com.order.domain.db.OrderEntityMapper;
import com.order.domain.OrderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessor orderProcessor;

    private final OrderEntityMapper orderEntityMapper;

    @PostMapping
    public OrderDto create(@RequestBody CreateOrderRequestDto request) {
        log.info("Created order: request {}", request);
        var saved = orderProcessor.create(request);
        return orderEntityMapper.toOrderDto(saved);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        log.info("Got one order with id {}", id);
        var found = orderProcessor.getOrderOrThrow(id);
        return orderEntityMapper.toOrderDto(found);
    }

    @PostMapping("/{id}/pay")
    public OrderDto payOrder(@PathVariable("id") Long id,
            @RequestBody OrderPaymentRequest request) {
        log.info("Paying order with: with id {} request {}", id, request);
        var saved = orderProcessor.processPayment(id, request);
        return orderEntityMapper.toOrderDto(saved);
    }
}
