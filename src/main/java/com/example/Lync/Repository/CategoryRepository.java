package com.example.Lync.Repository;

import com.example.Lync.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);
    boolean existsByCategoryName(String categoryName);

}

