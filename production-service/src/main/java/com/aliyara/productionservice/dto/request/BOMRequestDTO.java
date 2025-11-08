package com.aliyara.productionservice.dto.request;


import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BOMRequestDTO {
    private String productId;
    private List<MaterialsDTO> materials;
}
