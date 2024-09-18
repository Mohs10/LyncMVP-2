package com.example.Lync.ServiceImpl;

import com.example.Lync.DTO.ProductDTO;
import com.example.Lync.Entity.Category;
import com.example.Lync.Entity.Product;
import com.example.Lync.Entity.Type;
import com.example.Lync.Entity.Variety;
import com.example.Lync.Repository.CategoryRepository;
import com.example.Lync.Repository.ProductRepository;
import com.example.Lync.Repository.TypeRepository;
import com.example.Lync.Repository.VarietyRepository;
import com.example.Lync.Service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VarietyRepository varietyRepository;
    private final TypeRepository typeRepository;

//    @Value("${file.upload-dir}")
//    private String uploadDir;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
                              VarietyRepository varietyRepository, TypeRepository typeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.varietyRepository = varietyRepository;
        this.typeRepository = typeRepository;
    }

    @Override
    public String addProduct(ProductDTO productDTO, MultipartFile productImage1, MultipartFile productImage2) throws IOException {
        Product product = new Product();
        product.setProductName(productDTO.getProductName());

        // Set category
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        product.setCategory(category);

        // Set variety
        Variety variety = varietyRepository.findById(productDTO.getVarietyId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid variety ID"));
        product.setVariety(variety);

        // Set types
        List<Type> types = new ArrayList<>();
        for (String typeName : productDTO.getTypeNames()) {
            Type type = typeRepository.findByTypeName(typeName);
            if (type == null) {
                type = new Type();
                type.setTypeName(typeName);
                typeRepository.save(type);
            }
            types.add(type);
        }
        product.setTypes(types);

//        // Save product images
//        if (!productImage1.isEmpty()) {
//            String imageUrl1 = saveImage(productImage1);
//            product.setProductImageUrl1(imageUrl1);
//        }
//
//        if (!productImage2.isEmpty()) {
//            String imageUrl2 = saveImage(productImage2);
//            product.setProductImageUrl2(imageUrl2);
//        }

//        product.setPrice(productDTO.getPrice());
//        product.setQuantity(productDTO.getQuantity());

        productRepository.save(product);

        return "Product added successfully!";
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public String addProductTemp(ProductDTO productDTO) {
        Product product = new Product();
        product.setProductName(productDTO.getProductName());

        // Set category
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        product.setCategory(category);

        // Set variety
        Variety variety = varietyRepository.findById(productDTO.getVarietyId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid variety ID"));
        product.setVariety(variety);

        // Set types
        List<Type> types = new ArrayList<>();
        for (String typeName : productDTO.getTypeNames()) {
            Type type = typeRepository.findByTypeName(typeName);
            if (type == null) {
                type = new Type();
                type.setTypeName(typeName);
                typeRepository.save(type);
            }
            types.add(type);
        }
        product.setTypes(types);

        // Save product images
//        if (!productImage1.isEmpty()) {
//            String imageUrl1 = saveImage(productImage1);
//            product.setProductImageUrl1(imageUrl1);
//        }
//
//        if (!productImage2.isEmpty()) {
//            String imageUrl2 = saveImage(productImage2);
//            product.setProductImageUrl2(imageUrl2);
//        }




        productRepository.save(product);

        return "Product added successfully!";
    }

//    private String saveImage(MultipartFile file) throws IOException {
//        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//        Path filePath = Paths.get(uploadDir + fileName);
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//        return "/uploads/" + fileName;
//    }
}
