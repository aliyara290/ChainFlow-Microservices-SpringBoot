package com.aliyara.supplyservice.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table (name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column (name = "first_name", nullable = false)
    private String firstName;

    @Column (name = "last_name")
    private String lastName;

    private String phone;

    private String email;

    private Double rating;

    @Column (name = "lead_time")
    private Integer leadTime;
}
