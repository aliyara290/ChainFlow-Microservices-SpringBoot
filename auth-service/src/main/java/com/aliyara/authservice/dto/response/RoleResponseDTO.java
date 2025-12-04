package com.aliyara.authservice.dto.response;

import java.util.Set;

public record RoleResponseDTO(
        String id,
        String name,
        Set<AuthorityResponseDTO> authorities
) {
}
