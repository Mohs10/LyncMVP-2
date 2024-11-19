package com.example.Lync.Controller;

import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.ProductDTO;
import com.example.Lync.DTO.SellerProductImageDto;
import com.example.Lync.Entity.*;
import com.example.Lync.Repository.ProductRepository;
import com.example.Lync.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = {"http://localhost:5173", "http://lync-reactjs-bucket.s3-website.ap-south-1.amazonaws.com", "https://another-domain.com"})
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final S3Service s3Service;

    private final CategoryService categoryService;
    private final VarietyService varietyService;
    private final TypeService typeService;
    private final ProductRepository productRepository;
    private final FormService formService;

    public ProductController(ProductService productService, S3Service s3Service, CategoryService categoryService,
                             VarietyService varietyService, TypeService typeService, ProductRepository productRepository, FormService formService) {
        this.productService = productService;
        this.s3Service = s3Service;
        this.categoryService = categoryService;
        this.varietyService = varietyService;
        this.typeService = typeService;
        this.productRepository = productRepository;
        this.formService = formService;
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
    public ResponseEntity<Product> addProduct(@RequestBody ProductDTO productDTO) {
        System.out.println(productDTO);
        try {
            // Call the service layer to save the product
            Product product = productService.addProduct(productDTO);

            System.out.println(product);
            return new ResponseEntity<>(product, HttpStatus.CREATED);  // Return the created product
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/uploadProductImage/{productId}")
    public ResponseEntity<String> uploadProductImage(@PathVariable Long productId,
                                                     @RequestParam("productImage") MultipartFile productImage) throws IOException {

        System.out.println("HereImage");
        try {
            if (productImage != null && !productImage.isEmpty()) {
                // Upload the product image and update the product with the image URL
                String imageUrl = productService.uploadProductPicture(productId, productImage);
                return ResponseEntity.ok("Product image uploaded successfully at : " + imageUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product image is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading product image.");
        }
    }



    @GetMapping("/products/all")
    public List<?> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/inactiveProducts")
    public List<Product> getAllInactiveProducts(){
        return productService.getAllInactiveProducts();
    }

    // ---------------- Category Endpoints ---------------- //
    @PostMapping("/categories/add")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

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

    // ---------------- Form Endpoints ---------------- //
    @PostMapping("/forms/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<String> addForm(@RequestBody Form form) {
        return ResponseEntity.ok(formService.addForm(form));
    }

    @GetMapping("/forms/all")
    public List<Form> getAllForms() {
        return formService.getAllForm();
    }


    // ---------------- Types Endpoints ---------------- //

    @PostMapping("/types/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<String> addType(@RequestBody Type type) {
        return ResponseEntity.ok(typeService.addType(type));
    }

    @GetMapping("/types/all")
    public List<Type> getAllTypes() {
        return typeService.getAllTypes();
    }

    @GetMapping("/products/{productId}/images/presigned-urls")

    public ResponseEntity<Map<String, String>> getProductImagesPresignedUrls(
            @PathVariable Long productId) {
        Map<String, String> presignedUrls = s3Service.getProductImagesPresignedUrls(productId);
        return ResponseEntity.ok(presignedUrls); // Return presigned URLs for the images
    }


    // Search Product by Name
    // API to search products by name prefix
    @GetMapping("/search/prefix/{prefix}")
    public ResponseEntity<List<Product>> searchProductsByPrefix(@PathVariable String prefix) {
        List<Product> products = productService.searchProductsByPrefixOriginal(prefix);
        return ResponseEntity.ok(products);
    }




    // API to get products by category ID without sorting
    @GetMapping("/productByCategory/{categoryId}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<Product> products = productService.findByCategoryId(categoryId);

            // Return a structured response with products and success message
            return new ResponseEntity<>(
                    Map.of(
                            "status", HttpStatus.OK.value(),
                            "message", "Products fetched successfully",
                            "data", products
                    ), HttpStatus.OK
            );
        } catch (RuntimeException e) {
            // Return a 404 NOT FOUND response if no products are found
            return new ResponseEntity<>(
                    Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "message", e.getMessage()
                    ), HttpStatus.NOT_FOUND
            );
        }
    }

    // API to get products by category ID with sorting
    @GetMapping("/category/{categoryId}/sorted")
    public ResponseEntity<Map<String, Object>> getSortedProductsByCategory(@PathVariable Long categoryId,
                                                                           @RequestParam("sortBy") String sortBy) {
        try {

            // Sort the products by the specified field
            List<Product> sortedProducts = productService.getSortedProductsByCategory(categoryId, sortBy);

            // Return a structured response with sorted products
            return new ResponseEntity<>(
                    Map.of(
                            "status", HttpStatus.OK.value(),
                            "message", "Products fetched and sorted successfully",
                            "data", sortedProducts
                    ), HttpStatus.OK
            );
        } catch (IllegalArgumentException e) {
            // Handle invalid sort field (e.g., not price, name, or quantity)
            return new ResponseEntity<>(
                    Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", e.getMessage()
                    ), HttpStatus.BAD_REQUEST
            );
        } catch (RuntimeException e) {
            // Handle case where products are not found for the category ID
            return new ResponseEntity<>(
                    Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "message", e.getMessage()
                    ), HttpStatus.NOT_FOUND
            );
        }
    }

    @PostMapping("/editProduct/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> editProduct(@PathVariable("productId") Long productId,
                                               @RequestBody ProductDTO productDTO) throws Exception {

        // Retrieve the existing product from the database
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

//        // Update the product details based on the incoming ProductDTO
//        if (productImage != null && !productImage.isEmpty()) {
//            // If a new image is uploaded, set it
//            productDTO.setProductImage(productImage);
//        }

        // Call the service layer to update the product
        Product updatedProduct = productService.editProduct(existingProduct, productDTO);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);  // Return the updated product
    }

    @GetMapping("/setActive")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> active(){
        productService.allActive();
        return ResponseEntity.ok("Success");
    }

    @PatchMapping("/inactivateProduct/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> inactivateProduct(@PathVariable Long productId) throws Exception {
        try {
            productService.inactiveProduct(productId);
            return ResponseEntity.ok("Product with ID " + productId + " inactivated successfully");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/activateProduct/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> activateProduct(@PathVariable Long productId) throws Exception {
        try {
            productService.activeProduct(productId);
            return ResponseEntity.ok("Product with ID " + productId + " activated successfully");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




    @PostMapping("/upload-images/{spId}")
    public ResponseEntity<?> uploadSellerProductImages(
            @PathVariable("spId") String spId, // Capture spId from the URL path
            @RequestParam(value = "productImageUrl1", required = false) MultipartFile productImageUrl1,
            @RequestParam(value = "productImageUrl2", required = false) MultipartFile productImageUrl2,
            @RequestParam(value = "productCertificationUrl", required = false) MultipartFile productCertificationUrl,
            @RequestParam(value = "npopCertification", required = false) MultipartFile npopCertification,
            @RequestParam(value = "nopCertification", required = false) MultipartFile nopCertification,
            @RequestParam(value = "euCertification", required = false) MultipartFile euCertification,
            @RequestParam(value = "gsdcCertification", required = false) MultipartFile gsdcCertification,
            @RequestParam(value = "ipmCertification", required = false) MultipartFile ipmCertification
    ) {
        try {
            // Initialize the image DTO and set values
            SellerProductImageDto imageDto = new SellerProductImageDto();
            imageDto.setSpId(spId);
            imageDto.setProductImageUrl1(productImageUrl1);
            imageDto.setProductImageUrl2(productImageUrl2);
            imageDto.setProductCertificationUrl(productCertificationUrl);
            imageDto.setNpopCertification(npopCertification);
            imageDto.setNopCertification(nopCertification);
            imageDto.setEuCertification(euCertification);
            imageDto.setGsdcCertification(gsdcCertification);
            imageDto.setIpmCertification(ipmCertification);

            // Call the service to handle the images
            SellerProduct updatedSellerProduct = s3Service.addSellerProductImages(imageDto);
            return ResponseEntity.ok(updatedSellerProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/image/{spId}")
    public ResponseEntity<?> getProductImageUrls(@PathVariable String spId) {
        try {
            Map<String, String> imageUrls = s3Service.getProductImageUrls(spId);
            return ResponseEntity.ok(imageUrls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }



}

