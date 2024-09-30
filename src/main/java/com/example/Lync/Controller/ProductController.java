package com.example.Lync.Controller;

import com.example.Lync.DTO.ProductDTO;
import com.example.Lync.Entity.Category;
import com.example.Lync.Entity.Product;
import com.example.Lync.Entity.Type;
import com.example.Lync.Entity.Variety;
import com.example.Lync.Service.CategoryService;
import com.example.Lync.Service.ProductService;
import com.example.Lync.Service.TypeService;
import com.example.Lync.Service.VarietyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")

public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final VarietyService varietyService;
    private final TypeService typeService;

    public ProductController(ProductService productService, CategoryService categoryService,
                             VarietyService varietyService, TypeService typeService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.varietyService = varietyService;
        this.typeService = typeService;
    }

    // ---------------- Product Endpoints ---------------- //
//    @PostMapping("/products/add")
//    public ResponseEntity<String> addProduct(
//            @RequestPart("productDTO") ProductDTO productDTO,
//            @RequestPart("productImage1") MultipartFile productImage1,
//            @RequestPart("productImage2") MultipartFile productImage2) throws IOException {
//        return ResponseEntity.ok(productService.addProduct(productDTO, productImage1, productImage2));
//    }

    @PostMapping("/products/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<String> addProduct(
            @RequestBody ProductDTO productDTO) throws IOException {
        return ResponseEntity.ok(productService.addProductTemp(productDTO));
    }

    @GetMapping("/products/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // ---------------- Category Endpoints ---------------- //
    @PostMapping("/categories/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<String> addCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.addCategory(category));
    }

    @GetMapping("/categories/all")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // ---------------- Variety Endpoints ---------------- //
    @PostMapping("/varieties/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<String> addVariety(@RequestBody Variety variety) {
        return ResponseEntity.ok(varietyService.addVariety(variety));
    }

    @GetMapping("/varieties/all")
    public List<Variety> getAllVarieties() {
        return varietyService.getAllVarieties();
    }

    // ---------------- Type Endpoints ---------------- //
    @PostMapping("/types/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addType(@RequestBody Type type) {
        return ResponseEntity.ok(typeService.addType(type));
    }

    @GetMapping("/types/all")
    public List<Type> getAllTypes() {
        return typeService.getAllTypes();
    }
}

