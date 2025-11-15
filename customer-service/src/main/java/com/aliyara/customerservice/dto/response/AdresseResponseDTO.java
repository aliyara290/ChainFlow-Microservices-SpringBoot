package com.aliyara.customerservice.dto.response;

import lombok.*;

@Data
@Builder
public class AdresseResponseDTO {
    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;
}
