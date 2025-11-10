package com.aliyara.customerservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table (name = "driver")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, name = "license_type")
    private String licenseType;

    @Column(nullable = false, name = "license_number")
    private String licenseNumber;

}
