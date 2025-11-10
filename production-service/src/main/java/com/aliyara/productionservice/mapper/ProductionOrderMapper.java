package com.aliyara.productionservice.mapper;

import com.aliyara.productionservice.dto.request.ProductionOrderRequestDTO;
import com.aliyara.productionservice.dto.response.ProductionOrderResponseDTO;
import com.aliyara.productionservice.model.ProductionOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductionOrderMapper {

    @Mapping(source = "product.id", target = "productId")
    ProductionOrderResponseDTO toResponse(ProductionOrder productionOrder);

    @Mapping(source = "productId", target = "product.id")
    ProductionOrder toEntity(ProductionOrderRequestDTO productionOrderRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "productId", target = "product.id")
    void updateEntityFromDTO(ProductionOrderRequestDTO productionOrderRequestDTO, @MappingTarget ProductionOrder productionOrder);
}