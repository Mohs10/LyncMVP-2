package com.example.Lync.DTO;

import lombok.Data;

@Data
public class SpecificationDTO {
    private String specificationName;  // Name of the specification (e.g., weight, dimensions)
    private String specificationValue; // Value of the specification (e.g., 100g, 50cm)
}
