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
    private String id;
    @NotEmpty(message = "First name is required!")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String firstName;
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String lastName;
    private String phone;
    private String email;
    private Double rating;
    private Integer leadTime;
}
