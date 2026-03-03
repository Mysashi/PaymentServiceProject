package com.common_libs.api.order;

import java.util.Set;

public record CreateOrderRequestDto(
        Long customerId,
        String address,
        Set<OrderItemRequestDto> items
        ) {

}
