package com.aliyara.customerservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
        private String licenseType;
    private String licenseNumber;
}