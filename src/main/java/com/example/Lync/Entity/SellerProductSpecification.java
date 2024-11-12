package com.example.Lync.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerSpecificationId;

    private String specificationName;  // Name of the specification (e.g., weight, dimensions, material, etc.)

    private String specificationValue; // Value of the specification (e.g., 100g, 50cm, cotton, etc.)
    private String specificationValueUnits;


}
