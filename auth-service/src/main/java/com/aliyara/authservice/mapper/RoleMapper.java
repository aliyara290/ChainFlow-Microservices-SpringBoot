package com.aliyara.authservice.mapper;

import com.aliyara.authservice.dto.request.RoleRequestDTO;
import com.aliyara.authservice.dto.response.RoleResponseDTO;
import com.aliyara.authservice.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponseDTO toResponse(Role role);
    Role toEntity(RoleRequestDTO requestDTO);
    void updateEntityFromDTO(RoleRequestDTO requestDTO, @MappingTarget Role role);
}
