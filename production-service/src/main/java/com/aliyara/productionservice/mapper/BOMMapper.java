package com.aliyara.productionservice.mapper;


import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.response.BOMResponseDTO;
import com.aliyara.productionservice.model.BOM;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BOMMapper {
    BOMResponseDTO toResponse(BOM bom);
    BOM toEntity(BOMRequestDTO requestDTO);
    void updateEntityFomDTO(BOMRequestDTO requestDTO, @MappingTarget BOM bom);
}
