package com.aliyara.supplyservice.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SupplierResponseDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Double rating;
    private Integer leadTime;
}