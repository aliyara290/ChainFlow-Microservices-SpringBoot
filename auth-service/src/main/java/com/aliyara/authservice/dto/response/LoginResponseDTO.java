package com.aliyara.authservice.dto.response;

import java.util.Set;

public record LoginResponseDTO(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<RoleResponseDTO> roles
) {
}