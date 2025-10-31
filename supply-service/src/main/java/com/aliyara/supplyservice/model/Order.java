package com.aliyara.supplyservice.model;

import com.aliyara.supplyservice.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column (name = "order_date")
    private LocalDate orderDate = LocalDate.now();

    @Column (name = "supplier_id", nullable = false)
    private String supplier_id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany (mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderMaterial> orderMaterials = new ArrayList<>();


    public void addMaterial(Material material, Integer quantity){

    }

}
