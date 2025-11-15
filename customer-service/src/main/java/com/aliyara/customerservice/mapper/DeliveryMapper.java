package com.aliyara.customerservice.mapper;

import com.aliyara.customerservice.dto.request.DeliveryRequestDTO;
import com.aliyara.customerservice.dto.response.DeliveryResponseDTO;
import com.aliyara.customerservice.model.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AdresseMapper.class, VehicleMapper.class, DriverMapper.class})
public interface DeliveryMapper {

    @Mapping(source = "order.id", target = "id")
    DeliveryResponseDTO toResponse(Delivery delivery);

    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "order", ignore = true)
    Delivery toEntity(DeliveryRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "order", ignore = true)
    void updateEntityFromDTO(DeliveryRequestDTO requestDTO, @MappingTarget Delivery delivery);
}