package com.aliyara.supplyservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column (name = "stock", nullable = false)
    private Integer stock;

    @Column (name = "stock_min", nullable = false)
    private Integer stockMin;

    private String unit;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany (mappedBy = "material")
    private List<OrderMaterial> orderMaterials = new ArrayList<>();
}