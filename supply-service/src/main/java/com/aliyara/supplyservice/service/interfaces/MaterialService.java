package com.aliyara.supplyservice.service.interfaces;

import com.aliyara.supplyservice.dto.request.MaterialRequestDTO;
import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.dto.response.MaterialResponseDTO;
import com.aliyara.supplyservice.dto.response.OrderResponseDTO;

public interface MaterialService extends GenericService<MaterialResponseDTO, MaterialRequestDTO> {
}
