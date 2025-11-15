package com.aliyara.customerservice.controller;

import com.aliyara.customerservice.dto.request.DriverRequestDTO;
import com.aliyara.customerservice.dto.response.DriverResponseDTO;
import com.aliyara.customerservice.model.Driver;
import com.aliyara.customerservice.service.interfaces.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverRequestDTO requestDTO) {
        DriverResponseDTO responseDTO = driverService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable String id,
            @Valid @RequestBody DriverRequestDTO requestDTO) {
        DriverResponseDTO responseDTO = driverService.update(requestDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAll();
        return ResponseEntity.ok().body(drivers);
    }
}
