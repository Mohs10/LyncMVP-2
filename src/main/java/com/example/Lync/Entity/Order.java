package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders") // Renaming table to "orders"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oId;
    private String status;
    private String buyerUId; // Reference to buyer (User)
    private String sellerUId; // Reference to seller (User)
    private Long pId; // Reference to Product
    private Double quantity;
    private Double price;
    private String stockLocation;
    private String deliveryLocation;
    private Double buyingPrice;
    private Double sellingPrice;
    private Double marginPrice;

    // Getters and Setters
}

