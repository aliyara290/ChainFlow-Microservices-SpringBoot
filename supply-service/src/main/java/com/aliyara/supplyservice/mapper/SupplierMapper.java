package com.aliyara.supplyservice.mapper;

import com.aliyara.supplyservice.dto.request.SupplierRequestDTO;
import com.aliyara.supplyservice.dto.response.SupplierResponseDTO;
import com.aliyara.supplyservice.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponseDTO toResponse(Supplier supplier);
    Supplier toEntity(SupplierRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(SupplierRequestDTO dto, @MappingTarget Supplier supplier);
}
