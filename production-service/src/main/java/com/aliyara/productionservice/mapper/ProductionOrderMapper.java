package com.aliyara.productionservice.mapper;


import com.aliyara.productionservice.dto.request.ProductionOrderRequestDTO;
import com.aliyara.productionservice.dto.response.ProductionOrderResponseDTO;
import com.aliyara.productionservice.model.ProductionOrder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper (componentModel = "spring")
public interface ProductionOrderMapper {
    ProductionOrderResponseDTO toResponse(ProductionOrder productionOrder);
    ProductionOrder toEntity(ProductionOrderRequestDTO productionOrderRequestDTO);
    void updateEntityFromDTO(ProductionOrderRequestDTO productionOrderRequestDTO, @MappingTarget ProductionOrder productionOrder);
}