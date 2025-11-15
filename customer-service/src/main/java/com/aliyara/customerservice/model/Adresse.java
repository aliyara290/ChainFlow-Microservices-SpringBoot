package com.aliyara.customerservice.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table (name = "adresse")
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String zip;

    @OneToOne(fetch = FetchType.LAZY)
    private Customer customer;
}