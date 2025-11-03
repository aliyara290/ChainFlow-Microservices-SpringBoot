package com.aliyara.productionservice.model;

import com.aliyara.productionservice.model.enums.ProductionOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ProductionOrders")
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private int id;

    @Enumerated(EnumType.STRING)
    private ProductionOrderStatus status;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @OneToMany
    @JoinColumn(name = "product_id", nullable = false)
    private List<Product> product;

}
