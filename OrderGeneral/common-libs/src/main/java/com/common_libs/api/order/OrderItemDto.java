package com.common_libs.api.order;



public record OrderItemDto(Long id, Long itemId, String itemName, Integer quantity, Integer priceAtPurchase) {
}