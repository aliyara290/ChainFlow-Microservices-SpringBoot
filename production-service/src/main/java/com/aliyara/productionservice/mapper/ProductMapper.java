package com.aliyara.productionservice.mapper;


import com.aliyara.productionservice.dto.request.ProductRequestDTO;
import com.aliyara.productionservice.dto.response.ProductResponseDTO;
import com.aliyara.productionservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDTO toResponse(Product product);
    Product toEntity(ProductRequestDTO productRequestDTO);
//    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ProductRequestDTO productRequestDTO, @MappingTarget Product product);
}
