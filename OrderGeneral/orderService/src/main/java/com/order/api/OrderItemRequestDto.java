package com.order.api;

public record OrderItemRequestDto(
        Long itemId, Integer quantity,
        String itemName) {
}