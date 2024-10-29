package com.example.Lync.Repository;

import com.example.Lync.Entity.SellerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerProductRepository extends JpaRepository<SellerProduct, String> {
    List<SellerProduct> findBySellerId(String sellerId);

    @Query("SELECT sp FROM SellerProduct sp WHERE sp.productId = :productId")
    List<SellerProduct> findByPId(@Param("productId") Long productId);

    Optional<SellerProduct> findBySellerIdAndProductId(String sellerId, Long productId);
}

