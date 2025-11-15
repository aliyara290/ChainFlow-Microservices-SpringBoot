package com.aliyara.customerservice.service.interfaces;

import com.aliyara.customerservice.dto.request.VehicleRequestDTO;
import com.aliyara.customerservice.dto.response.VehicleResponseDTO;

import java.util.List;

public interface VehicleService {

    VehicleResponseDTO create(VehicleRequestDTO requestDTO);
    VehicleResponseDTO update(VehicleRequestDTO requestDTO, String id);
    List<VehicleResponseDTO> getAll();
}
