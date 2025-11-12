package com.aliyara.customerservice.mapper;

import com.aliyara.customerservice.dto.request.DriverRequestDTO;
import com.aliyara.customerservice.dto.response.DriverResponseDTO;
import com.aliyara.customerservice.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    DriverResponseDTO toResponse(Driver driver);

    Driver toEntity(DriverRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(DriverRequestDTO requestDTO, @MappingTarget Driver driver);
}