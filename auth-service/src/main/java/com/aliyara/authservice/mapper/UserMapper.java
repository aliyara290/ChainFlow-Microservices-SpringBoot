package com.aliyara.authservice.mapper;


import com.aliyara.authservice.dto.request.UserRequestDTO;
import com.aliyara.authservice.dto.response.UserResponseDTO;
import com.aliyara.authservice.model.AppUser;
import org.apache.catalina.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponse(AppUser user);
    AppUser toEntity(UserRequestDTO requestDTO);
    void updateEntityFromDTO(UserRequestDTO requestDTO, @MappingTarget AppUser user);
}
