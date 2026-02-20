package com.order.api;

import com.order.domain.OrderItemEntity;

/**
 * DTO for {@link OrderItemEntity}
 */
public record OrderItemDto(Long id, Long itemId, String itemName, Integer quantity) {
}