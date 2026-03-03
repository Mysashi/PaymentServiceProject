package com.common_libs.api.order;

public record OrderItemRequestDto(
        Long itemId, Integer quantity,
        String itemName) {
}