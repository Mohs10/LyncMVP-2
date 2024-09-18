package com.example.Lync.Service;

import com.example.Lync.DTO.ProductDTO;
import com.example.Lync.Entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    String addProduct(ProductDTO productDTO, MultipartFile productImage1, MultipartFile productImage2) throws IOException, IOException;
    List<Product> getAllProducts();

    String addProductTemp(ProductDTO productDTO);
}

