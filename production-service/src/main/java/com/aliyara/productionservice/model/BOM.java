package com.aliyara.productionservice.model;


import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table (name = "BillOfMaterials")
public class BOM {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "material_id", nullable = false)
    private String materialId;

    @ManyToOne
    @JoinColumn (name = "product_id")
    private Product product;
}
