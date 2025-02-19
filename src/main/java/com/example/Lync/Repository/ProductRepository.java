package com.example.Lync.Repository;

import com.example.Lync.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryCategoryId(Long categoryId);
//    List<Product> findByVarietyVarietyId(Long varietyId);

    List<Product> findByActiveProductTrue();

    List<Product> findByActiveProductFalse();

    @Query("SELECT p.productName FROM Product p WHERE p.productId = :productId")
    Optional<String> findProductNameByProductId(@Param("productId") Long productId);


    @Procedure(name = "GetTopSellingProducts")
    List<Object[]> getTopSellingProducts();


}

