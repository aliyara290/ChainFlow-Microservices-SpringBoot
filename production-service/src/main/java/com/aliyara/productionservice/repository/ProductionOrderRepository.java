package com.aliyara.productionservice.repository;


import com.aliyara.productionservice.model.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, String> {
}
