package com.aliyara.customerservice.mapper;

import com.aliyara.customerservice.dto.request.VehicleRequestDTO;
import com.aliyara.customerservice.dto.response.VehicleResponseDTO;
import com.aliyara.customerservice.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    VehicleResponseDTO toResponse(Vehicle vehicle);

    Vehicle toEntity(VehicleRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(VehicleRequestDTO requestDTO, @MappingTarget Vehicle vehicle);
}