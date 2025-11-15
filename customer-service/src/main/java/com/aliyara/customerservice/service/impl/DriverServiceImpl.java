package com.aliyara.customerservice.service.impl;


import com.aliyara.customerservice.dto.request.DriverRequestDTO;
import com.aliyara.customerservice.dto.response.DriverResponseDTO;
import com.aliyara.customerservice.exception.NoRecordFoundException;
import com.aliyara.customerservice.exception.RecordNotFoundException;
import com.aliyara.customerservice.mapper.DriverMapper;
import com.aliyara.customerservice.model.Driver;
import com.aliyara.customerservice.model.Vehicle;
import com.aliyara.customerservice.repository.DriverRepository;
import com.aliyara.customerservice.service.interfaces.DriverService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public DriverResponseDTO create(DriverRequestDTO requestDTO) {
        Driver driver = driverMapper.toEntity(requestDTO);
        Driver savedDriver = driverRepository.save(driver);
        return driverMapper.toResponse(savedDriver);
    }

    @Override
    public DriverResponseDTO update(DriverRequestDTO requestDTO, String id) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Driver"));
        driverMapper.updateEntityFromDTO(requestDTO, existingDriver);
        Driver savedDriver = driverRepository.save(existingDriver);
        return driverMapper.toResponse(savedDriver);
    }

    @Override
    public List<DriverResponseDTO> getAll() {
        List<Driver> drivers = driverRepository.findAll();
        if (drivers.isEmpty()) {
            throw new NoRecordFoundException("No Drivers found!");
        }
        return drivers.stream().map(driverMapper::toResponse).collect(Collectors.toList());
    }
}
