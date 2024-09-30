package com.example.Lync.Repository;

import com.example.Lync.Entity.FavouriteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FavouriteCategoryRepository extends JpaRepository<FavouriteCategory, Long> {
    List<FavouriteCategory> findByUserId(String userId);
    List<FavouriteCategory> findByCategoryId(Long categoryId);
}
