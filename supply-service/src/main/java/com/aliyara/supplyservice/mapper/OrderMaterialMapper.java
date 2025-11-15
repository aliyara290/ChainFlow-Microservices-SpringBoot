package com.aliyara.supplyservice.mapper;

import com.aliyara.supplyservice.dto.response.OrderMaterialResponseDTO;
import com.aliyara.supplyservice.model.OrderMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMaterialMapper {

    @Mapping(source = "material.id", target = "materialId")
    @Mapping(source = "material.name", target = "materialName")
    @Mapping(source = "material.unit", target = "unit")
    OrderMaterialResponseDTO toResponse(OrderMaterial orderMaterial);
}