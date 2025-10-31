package com.aliyara.supplyservice.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table
@Entity (name = "order_material")
public class OrderMaterial {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "order_id", nullable = false)
    private Order order;

    @ManyToOne (fetch =  FetchType.LAZY)
    @JoinColumn (name = "material_id", nullable = false)
    private Material material;

    @Column (name = "quantity", nullable = false)
    private Integer quantity;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderMaterial)) return false;
        OrderMaterial that = (OrderMaterial) o;
        return order != null && order.equals(that.getOrder()) &&
                material != null && material.equals(that.getMaterial());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
