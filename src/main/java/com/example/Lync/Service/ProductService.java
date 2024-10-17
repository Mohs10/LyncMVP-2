package com.example.Lync.Service;

import com.example.Lync.DTO.ProductDTO;
import com.example.Lync.Entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    String addProduct(ProductDTO productDTO, MultipartFile productImage1, MultipartFile productImage2) throws IOException, IOException;
    List<Product> getAllProducts();
    List<Product> getAllInactiveProducts();
    String addProductTemp(ProductDTO productDTO);
    public String addImageByProductId(Long productId, String s3KeyImage1, String s3KeyImage2) ;
    public Product searchProductByName(String productName);
    public List<Product> searchProductsByPrefixOriginal(String prefix);

    public List<Product> searchProductsByPrefix(String prefix);
    public List<Product> getSortedProductsByCategory(Long categoryId, String sortBy);
    public List<Product> findByCategoryId(Long categoryId);

    void editProduct(Long productId, ProductDTO productDTO) throws Exception;

    void allActive();

    void inactiveProduct(Long productId) throws Exception;

    void activeProduct(Long productId) throws Exception;
}

