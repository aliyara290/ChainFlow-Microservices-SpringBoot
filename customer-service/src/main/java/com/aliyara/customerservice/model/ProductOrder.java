package com.aliyara.customerservice.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_order")
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(nullable = false, name = "product_id")
    private String productId;
}
