package com.example.Lync.ServiceImpl;

import com.example.Lync.Entity.Category;
import com.example.Lync.Repository.CategoryRepository;
import com.example.Lync.Service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public String addCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            return "Category already exists!";
        }
        categoryRepository.save(category);
        return "Category added successfully!";
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}