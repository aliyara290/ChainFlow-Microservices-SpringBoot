package com.aliyara.authservice.dto.response;

import java.util.Set;

public record UserResponseDTO(
        String id,
        String firstName,
        String lastName,
        String username,
        Set<RoleResponseDTO> roles
) {
}
