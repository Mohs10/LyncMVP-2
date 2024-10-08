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
import com.example.Lync.ReusablePackage.BinarySearch;
import com.example.Lync.ReusablePackage.Sorting;
import com.example.Lync.ReusablePackage.StringAndDateUtils;
import com.example.Lync.Service.ProductService;
import com.example.Lync.trie.Trie;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VarietyRepository varietyRepository;
    private final TypeRepository typeRepository;

    private final StringAndDateUtils stringAndDateUtils;
    private final Sorting<Product> sortingUtility = new Sorting<>();
    private final BinarySearch<Product> searchUtility = new BinarySearch<>();

    private final Trie productNameTrie = new Trie();
    @PostConstruct
    public void init() {
        loadNameCache();
    }

    // Load products into Trie
    public void loadNameCache() {
        List<Product> productList = productRepository.findByActiveProductTrue();
        for (Product product : productList) {
            productNameTrie.insert(product.getProductName().toLowerCase(), product);
        }
    }


//    @Value("${file.upload-dir}")
//    private String uploadDir;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
                              VarietyRepository varietyRepository, TypeRepository typeRepository, StringAndDateUtils stringAndDateUtils) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.varietyRepository = varietyRepository;
        this.typeRepository = typeRepository;
        this.stringAndDateUtils = stringAndDateUtils;
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
        return productRepository.findByActiveProductTrue();
    }



    // Get products by category ID and sort them based on provided criteria
    @Override
    public List<Product> getSortedProductsByCategory(Long categoryId, String sortBy) {
        List<Product> products = productRepository.findByCategoryCategoryId(categoryId);
        if (products.isEmpty()) {
            throw new RuntimeException("No products found for category ID: " + categoryId);
        }

        Product[] productArray = products.toArray(new Product[0]);

        Comparator<Product> comparator = switch (sortBy) {
            case "price" -> Comparator.comparingDouble(Product::getPrice);
            case "name" -> Comparator.comparing(Product::getProductName);
            case "quantity" -> Comparator.comparingInt(Product::getQuantity);
            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        };

        sortingUtility.quickSort(productArray, comparator, 0, productArray.length - 1);

        return List.of(productArray);
    }

    // Search products by name prefix
    @Override
    public List<Product> searchProductsByPrefix(String prefix) {
        return productNameTrie.searchByPrefix(prefix.toLowerCase());
    }

    // Search by exact product name


    @Override
    public Product searchProductByName(String productName) {
        return productNameTrie.searchByPrefix(productName.toLowerCase()).stream()
                .filter(product -> product.getProductName().equalsIgnoreCase(productName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> searchProductsByPrefixOriginal(String productName) {
        if (productName == null) {
            productName = "";
        }

        String thisProductName = productName.toLowerCase();

        return productRepository.findByActiveProductTrue().stream()
                .filter(product ->
                        stringAndDateUtils.isPartialMatch(product.getProductName().toLowerCase(), thisProductName))
                .collect(Collectors.toList());
    }


    // Separate method to find products by category ID

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategoryCategoryId(categoryId);
        if (products.isEmpty()) {
            throw new RuntimeException("No products found for category ID: " + categoryId);
        }
        return products;
    }

    @Override
    public void editProduct(Long productId, ProductDTO productDTO) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow(() -> new Exception("Product not found."));

        Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(null);
        Variety variety = varietyRepository.findById(productDTO.getVarietyId()).orElseThrow(null);

        List<Type> productTypes = product.getTypes();

        product.setProductName(productDTO.getProductName());
        product.setCategory(category);
        product.setVariety(variety);

        for (String typeName : productDTO.getTypeNames()) {
            Type type = typeRepository.findByTypeName(typeName);
            if (type == null) {
                type = new Type();
                type.setTypeName(typeName);
                typeRepository.save(type);
            }

            // Check if the type already exists in the product's types list before adding
            if (!productTypes.contains(type)) {
                productTypes.add(type);
            }
        }

        product.setTypes(productTypes);
        productRepository.save(product);
    }

    @Override
    public void allActive() {
        List<Product> products = productRepository.findAll();
        products.stream().filter(product -> !product.isActiveProduct())
                .forEach(product -> {
                    product.setActiveProduct(true);
                    productRepository.save(product);
                });
    }

    @Override
    public void inactiveProduct(Long productId) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new Exception("Product does not exist with Product ID: " + productId));
        if(product.isActiveProduct()){
            product.setActiveProduct(false);
            productRepository.save(product);
        }else {
            throw new Exception("Product with ID " + productId + " is already inactive");
        }
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



    public String addImageByProductId(Long productId, String s3KeyImage1, String s3KeyImage2) {
        // Find product by productId
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new RuntimeException("Product not found with id: " + productId));

        // Set image URLs
        product.setProductImageUrl1(s3KeyImage1);
        product.setProductImageUrl2(s3KeyImage2);

        // Save the updated product back to the database
        productRepository.save(product);

        return "Image Added Successfully";
    }

}
