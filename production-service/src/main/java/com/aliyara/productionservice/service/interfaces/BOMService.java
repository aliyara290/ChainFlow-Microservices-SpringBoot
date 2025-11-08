package com.aliyara.productionservice.service.interfaces;

import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.response.BOMResponseDTO;
import com.aliyara.productionservice.model.BOM;
import com.aliyara.productionservice.payload.ApiResponse;

import java.util.List;

public interface BOMService {
    ApiResponse<Void> create(BOMRequestDTO requestDTO);
    BOMResponseDTO  update(String id, BOMRequestDTO requestDTO);
    ApiResponse<Void> delete(String id);
    BOMResponseDTO  findById(String id);
    List<BOMResponseDTO> findAll();
}
