package com.example.Lync.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    private String hsnCode;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "product_varieties",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "variety_id")
    )
    private List<Variety> varieties;

    @ManyToMany
    @JoinTable(
            name = "product_forms",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "form_id")
    )
    private List<Form> forms;

    private String productImageUrl;

    @Column(columnDefinition = "TEXT")
    private String productDescription;

    private boolean activeProduct = true;

    // Many-to-many relationship with certifications
    @ManyToMany
    @JoinTable(
            name = "product_certifications",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "certification_id")
    )
    private List<Certification> certifications;  // Certifications for this product

    @ManyToMany
    @JoinTable(
            name = "product_specifications",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "specification_id")
    )
    private List<Specification> specifications;  // New Many-to-Many relationship with Specification
}




