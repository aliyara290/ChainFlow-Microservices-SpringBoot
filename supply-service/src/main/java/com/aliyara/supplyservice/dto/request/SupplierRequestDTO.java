package com.aliyara.supplyservice.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SupplierRequestDTO {
    @NotEmpty(message = "First name is required!")
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Double rating;
    private Integer leadTime;
}
