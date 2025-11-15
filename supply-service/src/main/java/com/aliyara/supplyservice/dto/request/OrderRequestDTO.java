package com.aliyara.supplyservice.dto.request;

import com.aliyara.supplyservice.model.Material;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Builder
@Setter
@Getter
public class OrderRequestDTO {
    private String supplierId;
    private List<OrderMaterialRequestDTO> materials;
}
