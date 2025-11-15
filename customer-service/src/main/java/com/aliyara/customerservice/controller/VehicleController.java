package com.aliyara.customerservice.controller;

import com.aliyara.customerservice.dto.request.VehicleRequestDTO;
import com.aliyara.customerservice.dto.response.VehicleResponseDTO;
import com.aliyara.customerservice.model.Vehicle;
import com.aliyara.customerservice.service.interfaces.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> createVehicle(@RequestBody VehicleRequestDTO requestDTO) {
        VehicleResponseDTO responseDTO = vehicleService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable String id,
            @RequestBody VehicleRequestDTO requestDTO) {
        VehicleResponseDTO responseDTO = vehicleService.update(requestDTO, id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        List<VehicleResponseDTO> vehicles = vehicleService.getAll();
        return ResponseEntity.ok().body(vehicles);
    }
}
