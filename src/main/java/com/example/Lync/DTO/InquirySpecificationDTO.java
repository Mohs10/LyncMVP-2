package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquirySpecificationDTO {

    private Long inquirySpecificationId;
    private String qId;
    private String specificationName;
    private String specificationValue;
    private String specificationValueUnits;
}
