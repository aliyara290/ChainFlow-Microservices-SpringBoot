package com.aliyara.customerservice.repository;

import com.aliyara.customerservice.model.Vehicle;
import com.aliyara.customerservice.model.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    Optional<Vehicle> findByPlateNumber(String plateNumber);
    boolean existsByPlateNumber(String plateNumber);
    List<Vehicle> findByStatus(VehicleStatus status);
}