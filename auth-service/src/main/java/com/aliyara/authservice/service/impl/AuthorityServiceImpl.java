package com.aliyara.authservice.service.impl;

import com.aliyara.authservice.dto.request.AuthorityRequestDTO;
import com.aliyara.authservice.dto.response.AuthorityResponseDTO;
import com.aliyara.authservice.mapper.AuthorityMapper;
import com.aliyara.authservice.model.Authority;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.repository.AuthorityRepository;
import com.aliyara.authservice.service.interfaces.AuthorityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityMapper authorityMapper;

    @Override
    public AuthorityResponseDTO create(AuthorityRequestDTO requestDTO) {
        log.info("Creating new authority with name: {}", requestDTO.getName());

        if (authorityRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Authority with name '" + requestDTO.getName() + "' already exists");
        }

        Authority authority = authorityMapper.toEntity(requestDTO);
        Authority savedAuthority = authorityRepository.save(authority);

        log.info("Authority created successfully with id: {}", savedAuthority.getId());
        return authorityMapper.toResponse(savedAuthority);
    }

    @Override
    public AuthorityResponseDTO update(AuthorityRequestDTO requestDTO, String id) {
        log.info("Updating authority with id: {}", id);

        Authority authority = authorityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Authority not found with id: " + id));

        if (!authority.getName().equals(requestDTO.getName())
                && authorityRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Authority with name '" + requestDTO.getName() + "' already exists");
        }

        authorityMapper.updateEntityFromDTO(requestDTO, authority);
        Authority updatedAuthority = authorityRepository.save(authority);

        log.info("Authority updated successfully with id: {}", id);
        return authorityMapper.toResponse(updatedAuthority);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        log.info("Deleting authority with id: {}", id);

        Authority authority = authorityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Authority not found with id: " + id));

        if (authority.getRoles() != null && !authority.getRoles().isEmpty()) {
            throw new IllegalStateException("Cannot delete authority that is assigned to roles");
        }

        authorityRepository.delete(authority);

        log.info("Authority deleted successfully with id: {}", id);
        return new ApiResponse<>(true, "Authority deleted successfully", null);
    }

    @Override
    public List<AuthorityResponseDTO> getAll() {
        log.info("Fetching all authorities");

        List<Authority> authorities = authorityRepository.findAll();

        return authorities.stream()
                .map(authorityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorityResponseDTO findById(String id) {
        log.info("Fetching authority with id: {}", id);

        Authority authority = authorityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Authority not found with id: " + id));

        return authorityMapper.toResponse(authority);
    }
}