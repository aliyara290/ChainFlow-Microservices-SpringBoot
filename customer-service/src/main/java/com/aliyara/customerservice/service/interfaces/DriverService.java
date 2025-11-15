package com.aliyara.customerservice.service.interfaces;

import com.aliyara.customerservice.dto.request.DriverRequestDTO;
import com.aliyara.customerservice.dto.response.DriverResponseDTO;

import java.util.List;

public interface DriverService {

    DriverResponseDTO create(DriverRequestDTO requestDTO);
    DriverResponseDTO update(DriverRequestDTO requestDTO, String id);
    List<DriverResponseDTO> getAll();
}
