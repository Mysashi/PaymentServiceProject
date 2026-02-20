package com.order.api;

import com.order.domain.OrderEntity;
import com.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link OrderEntity}
 */
public record OrderDto(Long id, Long customerId, String address, BigDecimal totalAmount, String courierName,
                       Integer etaMinutes, OrderStatus orderStatus, Set<OrderItemDto> orderItemEntities) {
}