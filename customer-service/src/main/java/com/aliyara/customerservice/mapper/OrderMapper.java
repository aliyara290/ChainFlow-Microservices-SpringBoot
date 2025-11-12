package com.aliyara.customerservice.mapper;

import com.aliyara.customerservice.dto.request.OrderRequestDTO;
import com.aliyara.customerservice.dto.response.OrderResponseDTO;
import com.aliyara.customerservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {DeliveryMapper.class})
public interface OrderMapper {

    @Mapping(source = "customer.id", target = "customerId")
    OrderResponseDTO toResponse(Order order);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(target = "productOrders", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    Order toEntity(OrderRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(target = "productOrders", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    void updateEntityFromDTO(OrderRequestDTO requestDTO, @MappingTarget Order order);
}