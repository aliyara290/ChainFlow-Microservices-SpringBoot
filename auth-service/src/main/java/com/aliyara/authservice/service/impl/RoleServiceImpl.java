package com.aliyara.authservice.service.impl;

import com.aliyara.authservice.dto.request.RoleRequestDTO;
import com.aliyara.authservice.dto.response.RoleResponseDTO;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.service.interfaces.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    @Override
    public RoleResponseDTO create(RoleRequestDTO requestDTO) {
        return null;
    }

    @Override
    public RoleResponseDTO update(RoleRequestDTO requestDTO, String id) {
        return null;
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        return false;
    }

    @Override
    public List<RoleResponseDTO> getAll() {
        return List.of();
    }

    @Override
    public RoleResponseDTO findById(String id) {
        return null;
    }
}
