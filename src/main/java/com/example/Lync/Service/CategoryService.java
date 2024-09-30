package com.example.Lync.Service;
import com.example.Lync.Entity.Category;

import java.util.List;

public interface CategoryService {
    String addCategory(Category category);
    List<Category> getAllCategories();
}

