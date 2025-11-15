package com.aliyara.supplyservice.mapper;


import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.dto.response.OrderResponseDTO;
import com.aliyara.supplyservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring", uses = {OrderMaterialMapper.class})
public interface OrderMapper {

    OrderResponseDTO toResponse(Order order);

    Order toEntity(OrderRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(OrderRequestDTO requestDTO, @MappingTarget Order order);
}
