package com.order.domain.db;

import com.common_libs.api.order.CreateOrderRequestDto;
import com.common_libs.api.order.OrderDto;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OrderEntityMapper {


    OrderEntity toEntity(CreateOrderRequestDto orderDto);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }

    OrderDto toOrderDto(OrderEntity orderEntity);
}