package com.aliyara.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class AuthorityRequestDTO {
    @NotBlank(message = "Authority name is required")
    @Size(min = 3, max = 50, message = "Authority name must be between 3 and 50 characters")
    private String name;
}
