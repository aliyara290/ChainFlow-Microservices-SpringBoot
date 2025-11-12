package com.aliyara.customerservice.mapper;

import com.aliyara.customerservice.dto.request.AdresseRequestDTO;
import com.aliyara.customerservice.dto.response.AdresseResponseDTO;
import com.aliyara.customerservice.model.Adresse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdresseMapper {
    AdresseResponseDTO toResponse(Adresse adresse);

    Adresse toEntity(AdresseRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(AdresseRequestDTO requestDTO, @MappingTarget Adresse adresse);
}