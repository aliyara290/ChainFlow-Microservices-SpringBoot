package com.aliyara.customerservice.mapper;

import com.aliyara.customerservice.dto.request.CustomerRequestDTO;
import com.aliyara.customerservice.dto.response.CustomerResponseDTO;
import com.aliyara.customerservice.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AdresseMapper.class})
public interface CustomerMapper {
    CustomerResponseDTO toResponse(Customer customer);

    Customer toEntity(CustomerRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CustomerRequestDTO requestDTO, @MappingTarget Customer customer);
}