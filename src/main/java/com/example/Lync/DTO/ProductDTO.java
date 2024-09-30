package com.example.Lync.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private String productName;
    private Long categoryId;
    private Long varietyId;
    private List<String> typeNames;
}

