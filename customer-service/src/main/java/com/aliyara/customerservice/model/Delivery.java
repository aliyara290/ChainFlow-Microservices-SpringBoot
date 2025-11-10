package com.aliyara.customerservice.model;

import com.aliyara.customerservice.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table (name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double cost;

    @OneToOne(fetch = FetchType.EAGER)
    private Adresse adresse2;

    @OneToOne(fetch = FetchType.EAGER)
    private Vehicle vehicle;

    @OneToOne(fetch = FetchType.EAGER)
    private Driver driver;

    @OneToOne(fetch = FetchType.EAGER)
    private Order order;
}
