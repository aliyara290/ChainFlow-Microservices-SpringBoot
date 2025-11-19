package com.aliyara.productionservice.repository;

import com.aliyara.productionservice.model.BOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BOMRepository extends JpaRepository<BOM, String> {
}
