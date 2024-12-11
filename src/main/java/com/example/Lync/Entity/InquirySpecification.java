package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquirySpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquirySpecificationId;
    private String qId;
    private String specificationName;
    private String specificationValue;
    private String specificationValueUnits;

}
