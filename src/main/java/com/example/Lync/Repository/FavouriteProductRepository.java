package com.example.Lync.Repository;

import com.example.Lync.Entity.FavouriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteProductRepository extends JpaRepository<FavouriteProduct, Long> {
    List<FavouriteProduct> findByUserId(String userId);
    List<FavouriteProduct> findByProductId(Long productId);
}