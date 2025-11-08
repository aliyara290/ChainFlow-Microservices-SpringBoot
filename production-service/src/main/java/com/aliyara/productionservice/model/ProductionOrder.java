package com.aliyara.productionservice.model;

import com.aliyara.productionservice.model.enums.ProductionOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ProductionOrders")
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private ProductionOrderStatus status;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
