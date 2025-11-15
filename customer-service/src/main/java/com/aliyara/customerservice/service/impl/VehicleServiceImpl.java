package com.aliyara.customerservice.service.impl;

import com.aliyara.customerservice.dto.request.VehicleRequestDTO;
import com.aliyara.customerservice.dto.response.VehicleResponseDTO;
import com.aliyara.customerservice.exception.NoRecordFoundException;
import com.aliyara.customerservice.exception.RecordNotFoundException;
import com.aliyara.customerservice.mapper.VehicleMapper;
import com.aliyara.customerservice.model.Vehicle;
import com.aliyara.customerservice.repository.VehicleRepository;
import com.aliyara.customerservice.service.interfaces.VehicleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    public VehicleResponseDTO create(VehicleRequestDTO requestDTO) {
        Vehicle vehicle = vehicleMapper.toEntity(requestDTO);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(savedVehicle);
    }

    @Override
    public VehicleResponseDTO update(VehicleRequestDTO requestDTO, String id) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Vehicle"));
        vehicleMapper.updateEntityFromDTO(requestDTO, existingVehicle);
        Vehicle savedVehicle = vehicleRepository.save(existingVehicle);
        return vehicleMapper.toResponse(savedVehicle);
    }

    @Override
    public List<VehicleResponseDTO> getAll() {
       List<Vehicle> vehicles = vehicleRepository.findAll();
       if (vehicles.isEmpty()) {
           throw new NoRecordFoundException("No Vehicles found!");
       }
       return vehicles.stream().map(vehicleMapper::toResponse).collect(Collectors.toList());
    }
}
