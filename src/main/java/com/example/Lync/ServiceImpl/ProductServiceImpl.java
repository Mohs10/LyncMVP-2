package com.example.Lync.ServiceImpl;

import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.CertificationDTO;
import com.example.Lync.DTO.ProductDTO;
import com.example.Lync.DTO.SpecificationDTO;
import com.example.Lync.Entity.*;
import com.example.Lync.Repository.*;
import com.example.Lync.ReusablePackage.BinarySearch;
import com.example.Lync.ReusablePackage.Sorting;
import com.example.Lync.ReusablePackage.StringAndDateUtils;
import com.example.Lync.Service.ProductService;
import com.example.Lync.trie.Trie;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VarietyRepository varietyRepository;
    private final TypeRepository typeRepository;
    private final CertificationRepository certificationRepository;
    private final FormRepository formRepository;
    private final StringAndDateUtils stringAndDateUtils;
    private final Sorting<Product> sortingUtility = new Sorting<>();
    private final BinarySearch<Product> searchUtility = new BinarySearch<>();

    private final S3Service s3Service;
    private final Trie productNameTrie = new Trie();

    private static final AtomicInteger counter = new AtomicInteger(0);

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
                              VarietyRepository varietyRepository, TypeRepository typeRepository, CertificationRepository certificationRepository, FormRepository formRepository, StringAndDateUtils stringAndDateUtils, S3Service s3Service) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.varietyRepository = varietyRepository;
        this.typeRepository = typeRepository;
        this.certificationRepository = certificationRepository;
        this.formRepository = formRepository;
        this.stringAndDateUtils = stringAndDateUtils;
        this.s3Service = s3Service;
    }

    @Override
    public Product addProduct(ProductDTO productDTO) throws IOException {
        // Retrieve the category, varieties, forms, and certifications
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<Variety> varieties = varietyRepository.findAllById(productDTO.getVarietyIds());
        List<Form> forms = formRepository.findAllById(productDTO.getFormIds());

        // Map certifications
        List<Certification> certifications = new ArrayList<>();
        for (CertificationDTO certificationDTO : productDTO.getCertifications()) {
            Certification certification = new Certification();
            certification.setCertificationName(certificationDTO.getCertificationName());
            certification.setIsCertified(certificationDTO.getIsCertified());
            certifications.add(certificationRepository.save(certification));  // Save each certification
        }

        List<Specification> specifications = new ArrayList<>();
        for (SpecificationDTO specificationDTO : productDTO.getSpecifications()) {
            Specification specification = new Specification();
            specification.setSpecificationName(specificationDTO.getSpecificationName());
            specification.setSpecificationValue(specificationDTO.getSpecificationValue());
            specifications.add(specification);
        }


        productDTO.setProductId(generateUniqueProductId());

        if (productDTO.getProductImage() != null) {
            String profilePictureUrl =s3Service. uploadProductImage(productDTO.getProductId(), productDTO.getProductImage());
            productDTO.setProductImageUrl(profilePictureUrl);
        }

        // Create new product
        Product product = new Product();
        product.setProductId(productDTO.getProductId());
        product.setProductName(productDTO.getProductName());
        product.setHsnCode(productDTO.getHsnCode());
        product.setCategory(category);
        product.setVarieties(varieties);
        product.setForms(forms);
        product.setProductImageUrl(productDTO.getProductImageUrl());
        product.setProductDescription(productDTO.getProductDescription());
//        product.setActiveProduct(productDTO.isActiveProduct());
        product.setCertifications(certifications);  // Set the certifications
        product.setSpecifications(specifications);


        // Save the product
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findByActiveProductTrue();
    }

    @Override
    public List<Product> getAllInactiveProducts() {
        return productRepository.findByActiveProductFalse();
    }


    // Get products by category ID and sort them based on provided criteria
    @Override
    public List<Product> getSortedProductsByCategory(Long categoryId, String sortBy) {
        List<Product> products = productRepository.findByCategoryCategoryId(categoryId);
        if (products.isEmpty()) {
            throw new RuntimeException("No products found for category ID: " + categoryId);
        }

        Product[] productArray = products.toArray(new Product[0]);

//        Comparator<Product> comparator = switch (sortBy) {
//            case "price" -> Comparator.comparingDouble(Product::getPrice);
//            case "name" -> Comparator.comparing(Product::getProductName);
//            case "quantity" -> Comparator.comparingInt(Product::getQuantity);
//            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy);
//        };

//        sortingUtility.quickSort(productArray, comparator, 0, productArray.length - 1);

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
    public void editProduct(Long productId, ProductDTO productDTO){}
//    public void editProduct(Long productId, ProductDTO productDTO) throws Exception {
//        System.out.println("ProductDTO" + productDTO);
//        Product product = productRepository.findById(productId).orElseThrow(() -> new Exception("Product not found."));
//
//        Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(null);
//        Variety variety = varietyRepository.findById(productDTO.getVarietyId()).orElseThrow(null);
//
////        List<Type> productTypes = product.getTypes();
//
//        product.setProductName(productDTO.getProductName());
//        product.setCategory(category);
////        product.setVariety(variety);
//
//        for (String typeName : productDTO.getTypeNames()) {
//            Type type = typeRepository.findByTypeName(typeName);
//            if (type == null) {
//                type = new Type();
//                type.setTypeName(typeName);
//                typeRepository.save(type);
//            }
//
//            // Check if the type already exists in the product's types list before adding
////            if (!productTypes.contains(type)) {
////                productTypes.clear();
////                productTypes.add(type);
////            }
//        }
//
////        product.setTypes(productTypes);
//        productRepository.save(product);
//    }

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
    public void activeProduct(Long productId) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new Exception("Product does not exist with Product ID: " + productId));
        if(!product.isActiveProduct()){
            product.setActiveProduct(true);
            productRepository.save(product);
        }else {
            throw new Exception("Product with ID " + productId + " is already active");
        }
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
//        product.setProductImageUrl1(s3KeyImage1);
//        product.setProductImageUrl2(s3KeyImage2);

        // Save the updated product back to the database
        productRepository.save(product);

        return "Image Added Successfully";
    }








    // Method to generate unique Product ID
    public static long generateUniqueProductId() {
        // Get the current date in 'yyyyMMdd' format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());

        // Get current time in milliseconds
        long currentMillis = System.currentTimeMillis();

        // Increment the counter to get a unique number
        int uniqueNumber = counter.incrementAndGet();

        // Combine the date (yyyyMMdd), current time in millis, and the unique incremented counter
        String productIdStr = dateStr + currentMillis % 100000 + uniqueNumber;

        // Convert the final string to a long value and return
        return Long.parseLong(productIdStr);
    }





}
