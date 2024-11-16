package com.example.Lync.DTO;

import lombok.Data;

@Data
public class PriceRangeProjection {

   private Long productId;

    private Double maxPrice;
    private Double minPrice;

}
