package com.example.Lync.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long specificationId;

    private String specificationName;  // Name of the specification (e.g., weight, dimensions, material, etc.)

    private String specificationValue; // Value of the specification (e.g., 100g, 50cm, cotton, etc.)

}

