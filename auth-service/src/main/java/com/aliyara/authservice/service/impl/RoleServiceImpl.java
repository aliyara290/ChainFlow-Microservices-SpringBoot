package com.aliyara.authservice.service.impl;

import com.aliyara.authservice.dto.request.AuthorityRequestDTO;
import com.aliyara.authservice.dto.request.RoleRequestDTO;
import com.aliyara.authservice.dto.response.RoleResponseDTO;
import com.aliyara.authservice.exception.ResourceNotFoundException;
import com.aliyara.authservice.mapper.RoleMapper;
import com.aliyara.authservice.model.Authority;
import com.aliyara.authservice.model.Role;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.repository.AuthorityRepository;
import com.aliyara.authservice.repository.RoleRepository;
import com.aliyara.authservice.service.interfaces.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponseDTO create(RoleRequestDTO requestDTO) {
        log.info("Creating new role with name: {}", requestDTO.getName());

        if (roleRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Role with name '" + requestDTO.getName() + "' already exists");
        }

        Role role = roleMapper.toEntity(requestDTO);

        if (requestDTO.getAuthorities() != null && !requestDTO.getAuthorities().isEmpty()) {
            Set<Authority> authorities = new HashSet<>();
            for (AuthorityRequestDTO authorityDTO : requestDTO.getAuthorities()) {
                Authority authority = authorityRepository.findByName(authorityDTO.getName())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Authority not found with name: " + authorityDTO.getName()));
                authorities.add(authority);
            }
            role.setAuthorities(authorities);
        }

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with id: {}", savedRole.getId());
        return roleMapper.toResponse(savedRole);
    }

    @Override
    public RoleResponseDTO update(RoleRequestDTO requestDTO, String id) {
        log.info("Updating role with id: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (!role.getName().equals(requestDTO.getName())
                && roleRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Role with name '" + requestDTO.getName() + "' already exists");
        }

        role.setName(requestDTO.getName());

        if (requestDTO.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            for (AuthorityRequestDTO authorityDTO : requestDTO.getAuthorities()) {
                Authority authority = authorityRepository.findByName(authorityDTO.getName())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Authority not found with name: " + authorityDTO.getName()));
                authorities.add(authority);
            }
            role.setAuthorities(authorities);
        }

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated successfully with id: {}", id);
        return roleMapper.toResponse(updatedRole);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        log.info("Deleting role with id: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (role.getUsers() != null && !role.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete role that is assigned to users");
        }

        roleRepository.delete(role);
        log.info("Role deleted successfully with id: {}", id);
        return new ApiResponse<>(true, "Role deleted successfully", null);
    }

    @Override
    public List<RoleResponseDTO> getAll() {
        log.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO findById(String id) {
        log.info("Fetching role with id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return roleMapper.toResponse(role);
    }
}