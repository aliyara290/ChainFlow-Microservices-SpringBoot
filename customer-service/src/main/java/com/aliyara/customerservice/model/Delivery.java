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
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double cost;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Adresse adresse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Driver driver;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Order order;
}
