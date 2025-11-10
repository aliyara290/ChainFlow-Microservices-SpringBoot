package com.aliyara.customerservice.model;

import com.aliyara.customerservice.model.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(nullable = false, name = "plate_number")
    private String plateNumber;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private double capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private VehicleStatus status;

    @Column(nullable = false)
    private Integer year;
}
