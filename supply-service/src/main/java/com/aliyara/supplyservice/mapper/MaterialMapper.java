package com.aliyara.supplyservice.mapper;

import com.aliyara.supplyservice.dto.request.MaterialRequestDTO;
import com.aliyara.supplyservice.dto.response.MaterialResponseDTO;
import com.aliyara.supplyservice.model.Material;
import com.aliyara.supplyservice.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MaterialMapper {
    @Mapping(source = "supplier.id", target = "supplierId")
    MaterialResponseDTO toResponse(Material material);
    @Mapping(source = "supplierId", target = "supplier")
    Material toEntity(MaterialRequestDTO requestDTO);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(MaterialRequestDTO requestDTO, @MappingTarget Material material);
    default Supplier mapSupplier(String supplierId) {
        if (supplierId == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setId(supplierId);
        return supplier;
    }
}
