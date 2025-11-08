package com.aliyara.productionservice.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Entity
@Table (name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "production_name")
    private Integer productionTime;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "product")
    private List<BOM>  boms;
}
