package com.aliyara.authservice.mapper;

import com.aliyara.authservice.dto.request.AuthorityRequestDTO;
import com.aliyara.authservice.dto.request.RoleRequestDTO;
import com.aliyara.authservice.dto.response.AuthorityResponseDTO;
import com.aliyara.authservice.dto.response.RoleResponseDTO;
import com.aliyara.authservice.model.Authority;
import com.aliyara.authservice.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {
    AuthorityResponseDTO toResponse(Authority authority);
    Authority toEntity(AuthorityRequestDTO requestDTO);
    void updateEntityFromDTO(AuthorityRequestDTO requestDTO, @MappingTarget Authority authority);
}
