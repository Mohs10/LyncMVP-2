package com.example.Lync.ServiceImpl;

import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.Entity.*;

import com.example.Lync.Repository.*;
import com.example.Lync.Service.SellerBuyerService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SellerBuyerServiceImpl implements SellerBuyerService {

    private  final SellerBuyerRepository sellerBuyerRepository;

    private final UserInfoRepository userInfoRepository;

    private final FavouriteCategoryRepository favouriteCategoryRepository;

    private final TypeRepository typeRepository;
    private final ProductRepository productRepository;
    private final FavouriteProductRepository favouriteProductRepository;
    private final SellerProductRepository sellerProductRepository;

    private final S3Service s3Service;


    @Autowired
    @Lazy
    private PasswordEncoder encoder;


    public SellerBuyerServiceImpl(SellerBuyerRepository sellerBuyerRepository, UserInfoRepository userInfoRepository, FavouriteCategoryRepository favouriteCategoryRepository, TypeRepository typeRepository, ProductRepository productRepository, FavouriteProductRepository favouriteProductRepository, SellerProductRepository sellerProductRepository, S3Service s3Service) {
        this.sellerBuyerRepository = sellerBuyerRepository;
        this.userInfoRepository = userInfoRepository;
        this.favouriteCategoryRepository = favouriteCategoryRepository;
        this.typeRepository = typeRepository;
        this.productRepository = productRepository;
        this.favouriteProductRepository = favouriteProductRepository;
        this.sellerProductRepository = sellerProductRepository;
        this.s3Service = s3Service;
    }
    private final Map<String, SellerBuyer> sellerBuyerPhoneNumberCache = new HashMap<>();
    private final Map<String, SellerBuyer> sellerBuyerEmailCache = new HashMap<>();

    @PostConstruct
    public void init()
    {
        loadSellerBuyerEmailCache();
        loadSellerBuyerPhoneNumberCache();
    }
    public void loadSellerBuyerPhoneNumberCache() {

        List<SellerBuyer> userList = sellerBuyerRepository.findAll();
        for (SellerBuyer user : userList) {
            sellerBuyerPhoneNumberCache.put(user.getPhoneNumber(),user);

        }
    }

    public void loadSellerBuyerEmailCache() {

        List<SellerBuyer> userList = sellerBuyerRepository.findAll();
        for (SellerBuyer user : userList) {
            sellerBuyerEmailCache.put(user.getEmail(),user);

        }
    }
    private static final AtomicInteger counter = new AtomicInteger(0); // Sequential number generator

    public String generateUserId() {
        String prefix = "LYNC";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int uniqueNumber = counter.incrementAndGet(); // Increment to get a new unique number

        return String.format("%s%s%03d", prefix, datePart, uniqueNumber); // LYNCYYYYMMDDXXX
    }

    @Override
    public SellerBuyer findbyPhoneNumber(String phoneNumber) {
        return sellerBuyerPhoneNumberCache.get(phoneNumber);
    }

    @Override
    public void createSeller(SellerBuyerDTO sellerBuyerDTO) throws IOException {
        sellerBuyerDTO.setBuyer(false);
        sellerBuyerDTO.setSeller(true);
        sellerBuyerDTO.setUserId(generateUserId());
        LocalDateTime indianLocalDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        sellerBuyerDTO.setCreatedAt(indianLocalDateTime);


        // Upload profile picture if provided
        if (sellerBuyerDTO.getProfilePicture() != null) {
            String profilePictureUrl =s3Service. uploadUserImage(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getProfilePicture());
            sellerBuyerDTO.setProfilePictureUrl(profilePictureUrl);
        }

        // Upload certificate if provided
        if (sellerBuyerDTO.getCertificate() != null) {
            String certificateUrl = s3Service.uploadUserCertificate(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getCertificate());
            sellerBuyerDTO.setCertificateUrl(certificateUrl);
        }

        // Upload cancelled cheque if provided
        if (sellerBuyerDTO.getCancelledCheque() != null) {
            String cancelledChequeUrl = s3Service.uploadUserCancelledCheque(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getCancelledCheque());
            sellerBuyerDTO.setCancelledChequeUrl(cancelledChequeUrl);
        }



        SellerBuyer sellerBuyer=  sellerBuyerRepository.save(convertToSellerBuyer(sellerBuyerDTO));
        addToPhoneNumberCache(sellerBuyer.getPhoneNumber(),sellerBuyer);
        addToPhoneEmailCache(sellerBuyer.getEmail(),sellerBuyer);
        UserInfo userInfo = new UserInfo();

        userInfo.setEmail(sellerBuyer.getEmail());
        userInfo.setName(sellerBuyer.getFullName());
        userInfo.setRoles("ROLE_SELLER");
        userInfo.setMobileNumber(sellerBuyer.getPhoneNumber());
        userInfo.setPassword(encoder.encode(sellerBuyerDTO.getPassword()));
        userInfoRepository.save(userInfo);
//        Kishan

    }

    @Override
    public void createBuyer(SellerBuyerDTO sellerBuyerDTO) throws IOException {
        sellerBuyerDTO.setBuyer(true);
        sellerBuyerDTO.setSeller(false);
        sellerBuyerDTO.setUserId(generateUserId());

        LocalDateTime indianLocalDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        sellerBuyerDTO.setCreatedAt(indianLocalDateTime);

        // Upload profile picture if provided
        if (sellerBuyerDTO.getProfilePicture() != null) {
            String profilePictureUrl =s3Service. uploadUserImage(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getProfilePicture());
            sellerBuyerDTO.setProfilePictureUrl(profilePictureUrl);
        }

        // Upload certificate if provided
        if (sellerBuyerDTO.getCertificate() != null) {
            String certificateUrl = s3Service.uploadUserCertificate(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getCertificate());
            sellerBuyerDTO.setCertificateUrl(certificateUrl);
        }

        // Upload cancelled cheque if provided
        if (sellerBuyerDTO.getCancelledCheque() != null) {
            String cancelledChequeUrl = s3Service.uploadUserCancelledCheque(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getCancelledCheque());
            sellerBuyerDTO.setCancelledChequeUrl(cancelledChequeUrl);
        }


        SellerBuyer sellerBuyer=  sellerBuyerRepository.save(convertToSellerBuyer(sellerBuyerDTO));
        addToPhoneNumberCache(sellerBuyer.getPhoneNumber(),sellerBuyer);
        addToPhoneEmailCache(sellerBuyer.getEmail(),sellerBuyer);

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(sellerBuyer.getEmail());
        userInfo.setName(sellerBuyer.getFullName());
        userInfo.setRoles("ROLE_BUYER");
        userInfo.setMobileNumber(sellerBuyer.getPhoneNumber());
        userInfo.setPassword(encoder.encode(sellerBuyerDTO.getPassword()));
        userInfoRepository.save(userInfo);

    }

    @Override
    public void editSellerBuyer(String userId, SellerBuyerDTO sellerBuyerDTO) {
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(userId).orElseThrow(()->
                new RuntimeException("User not found with User ID: " + userId));
        String email = sellerBuyer.getEmail();


        // Check if the new email is in cache or already in use by another user
        if (!sellerBuyer.getEmail().equals(sellerBuyerDTO.getEmail())) {
            if (isEmailInCache(sellerBuyerDTO.getEmail())) {
                throw new RuntimeException("Email already exists in cache: " + sellerBuyerDTO.getEmail());
            }
//            if (existsByEmail(sellerBuyerDTO.getEmail())) {
//                throw new RuntimeException("Email is already in use by another user: " + sellerBuyerDTO.getEmail());
//            }
        }

        // Check if the new phone number is in cache or already in use by another user
        if (!sellerBuyer.getPhoneNumber().equals(sellerBuyerDTO.getPhoneNumber())) {
            if (isPhoneNumberInCache(sellerBuyerDTO.getPhoneNumber())) {
                throw new RuntimeException("Phone number already exists in cache: " + sellerBuyerDTO.getPhoneNumber());
            }
//            if (sellerBuyerRepository.existsByPhoneNumber(sellerBuyerDTO.getPhoneNumber())) {
//                throw new RuntimeException("Phone number is already in use by another user: " + sellerBuyerDTO.getPhoneNumber());
//            }
        }

        sellerBuyer.setFullName(sellerBuyerDTO.getFullName());
        sellerBuyer.setEmail(sellerBuyerDTO.getEmail());
        sellerBuyer.setPhoneNumber(sellerBuyerDTO.getPhoneNumber());
        sellerBuyer.setCountry(sellerBuyerDTO.getCountry());
        sellerBuyer.setState(sellerBuyerDTO.getState());
        sellerBuyer.setCity(sellerBuyerDTO.getCity());
        sellerBuyer.setPinCode(sellerBuyerDTO.getPinCode());
        sellerBuyer.setAddress(sellerBuyerDTO.getAddress());
//        sellerBuyer.setCreatedAt(sellerBuyerDTO.getCreatedAt());
        LocalDateTime indianLocalDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        sellerBuyer.setUpdatedAt(indianLocalDateTime);
        sellerBuyer.setSeller(sellerBuyerDTO.getSeller());
        sellerBuyer.setBuyer(sellerBuyerDTO.getBuyer());
//        sellerBuyer.setActiveUser(sellerBuyerDTO.getActiveUser());
        sellerBuyer.setPanNumber(sellerBuyerDTO.getPanNumber());
        sellerBuyer.setIncorporationDate(sellerBuyerDTO.getIncorporationDate());
        sellerBuyer.setCompanyName(sellerBuyerDTO.getCompanyName());
        sellerBuyer.setRegistrationNumber(sellerBuyerDTO.getRegistrationNumber());
        sellerBuyer.setCompanyEmail(sellerBuyerDTO.getCompanyEmail());
        sellerBuyer.setWareHouseAddress(sellerBuyerDTO.getWareHouseAddress());
        sellerBuyer.setGstIn(sellerBuyerDTO.getGstIn());
        sellerBuyer.setCompanyLocation(sellerBuyerDTO.getCompanyLocation());
        sellerBuyerDTO.setCompanyLocation(sellerBuyer.getCompanyLocation());
        sellerBuyerDTO.setCompanyCountry(sellerBuyer.getCompanyCountry());
        sellerBuyerDTO.setCompanyState(sellerBuyer.getCompanyState());
        sellerBuyerDTO.setCompanyCity(sellerBuyer.getCompanyCity());
        sellerBuyerDTO.setCompanyPinCode(sellerBuyer.getCompanyPinCode());
        sellerBuyerDTO.setWarehouseCountry(sellerBuyer.getWarehouseCountry());
        sellerBuyerDTO.setWarehouseState(sellerBuyer.getWarehouseState());
        sellerBuyerDTO.setWarehouseCity(sellerBuyer.getWarehouseCity());
        sellerBuyerDTO.setWarehousePinCode(sellerBuyer.getWarehousePinCode());


        SellerBuyer newSellerBuyer=   sellerBuyerRepository.save(sellerBuyer);


    UserInfo userInfo = userInfoRepository.findByEmail(email).orElseThrow(()->
            new RuntimeException("User not found with User Email: " + email));
        addToPhoneNumberCache(newSellerBuyer.getPhoneNumber(),sellerBuyer);
        addToPhoneEmailCache(newSellerBuyer.getEmail(),sellerBuyer);
        userInfo.setEmail(newSellerBuyer.getEmail());
        userInfo.setName(newSellerBuyer.getFullName());
        userInfo.setMobileNumber(newSellerBuyer.getPhoneNumber());
        userInfoRepository.save(userInfo);
    }


    private SellerBuyer convertToSellerBuyer(SellerBuyerDTO sellerBuyerDTO) {
        SellerBuyer sellerBuyer = new SellerBuyer();

        // Set basic fields
        sellerBuyer.setUserId(sellerBuyerDTO.getUserId());
        sellerBuyer.setCreatedAt(sellerBuyerDTO.getCreatedAt());
        sellerBuyer.setUpdatedAt(sellerBuyerDTO.getUpdatedAt());
        sellerBuyer.setFullName(sellerBuyerDTO.getFullName());
        sellerBuyer.setEmail(sellerBuyerDTO.getEmail());
//        sellerBuyer.setPassword(sellerBuyerDTO.getPassword());
        sellerBuyer.setPhoneNumber(sellerBuyerDTO.getPhoneNumber());
        sellerBuyer.setCountry(sellerBuyerDTO.getCountry());
        sellerBuyer.setState(sellerBuyerDTO.getState());
        sellerBuyer.setCity(sellerBuyerDTO.getCity());
        sellerBuyer.setPinCode(sellerBuyerDTO.getPinCode());
        sellerBuyer.setAddress(sellerBuyerDTO.getAddress());

        // Set role and status fields
        sellerBuyer.setSeller(sellerBuyerDTO.getSeller());
        sellerBuyer.setBuyer(sellerBuyerDTO.getBuyer());
        sellerBuyer.setPanNumber(sellerBuyerDTO.getPanNumber());

        // Set company details
        sellerBuyer.setIncorporationDate(sellerBuyerDTO.getIncorporationDate());
        sellerBuyer.setCompanyName(sellerBuyerDTO.getCompanyName());
        sellerBuyer.setRegistrationNumber(sellerBuyerDTO.getRegistrationNumber());
        sellerBuyer.setCompanyEmail(sellerBuyerDTO.getCompanyEmail());
        sellerBuyer.setGstIn(sellerBuyerDTO.getGstIn());
        sellerBuyer.setCompanyLocation(sellerBuyerDTO.getCompanyLocation());
        sellerBuyer.setWareHouseAddress(sellerBuyerDTO.getWareHouseAddress());

        // Set additional company location details
        sellerBuyer.setCompanyCountry(sellerBuyerDTO.getCompanyCountry());
        sellerBuyer.setCompanyState(sellerBuyerDTO.getCompanyState());
        sellerBuyer.setCompanyCity(sellerBuyerDTO.getCompanyCity());
        sellerBuyer.setCompanyPinCode(sellerBuyerDTO.getCompanyPinCode());

        // Set additional warehouse address details
        sellerBuyer.setWarehouseCountry(sellerBuyerDTO.getWarehouseCountry());
        sellerBuyer.setWarehouseState(sellerBuyerDTO.getWarehouseState());
        sellerBuyer.setWarehouseCity(sellerBuyerDTO.getWarehouseCity());
        sellerBuyer.setWarehousePinCode(sellerBuyerDTO.getWarehousePinCode());

        // Set waiver and document URLs
//        sellerBuyer.setWaiveSampleFree(sellerBuyerDTO.getWaiveSampleFree());
        sellerBuyer.setProfilePictureUrl(sellerBuyerDTO.getProfilePictureUrl());
        sellerBuyer.setCancelledChequeUrl(sellerBuyerDTO.getCancelledChequeUrl());
        sellerBuyer.setCertificateUrl(sellerBuyerDTO.getCertificateUrl());

        return sellerBuyer;
    }


    private SellerBuyerDTO convertToSellerBuyerDTO(SellerBuyer sellerBuyer) {
        if (sellerBuyer == null) {
            return null; // Handle null input
        }

        SellerBuyerDTO sellerBuyerDTO = new SellerBuyerDTO();
        sellerBuyerDTO.setUserId(sellerBuyer.getUserId());
        sellerBuyerDTO.setFullName(sellerBuyer.getFullName());
        sellerBuyerDTO.setEmail(sellerBuyer.getEmail());
        sellerBuyerDTO.setPhoneNumber(sellerBuyer.getPhoneNumber());
        sellerBuyerDTO.setCountry(sellerBuyer.getCountry());
        sellerBuyerDTO.setState(sellerBuyer.getState());
        sellerBuyerDTO.setCity(sellerBuyer.getCity());
        sellerBuyerDTO.setPinCode(sellerBuyer.getPinCode());
        sellerBuyerDTO.setAddress(sellerBuyer.getAddress());
        sellerBuyerDTO.setCreatedAt(sellerBuyer.getCreatedAt());
        sellerBuyerDTO.setUpdatedAt(sellerBuyer.getUpdatedAt());
        sellerBuyerDTO.setSeller(sellerBuyer.getSeller());
        sellerBuyerDTO.setBuyer(sellerBuyer.getBuyer());
        sellerBuyerDTO.setPanNumber(sellerBuyer.getPanNumber());

        sellerBuyerDTO.setIncorporationDate(sellerBuyer.getIncorporationDate());
        sellerBuyerDTO.setCompanyName(sellerBuyer.getCompanyName());
        sellerBuyerDTO.setRegistrationNumber(sellerBuyer.getRegistrationNumber());
        sellerBuyerDTO.setCompanyEmail(sellerBuyer.getCompanyEmail());
        sellerBuyerDTO.setWareHouseAddress(sellerBuyer.getWareHouseAddress());
        sellerBuyerDTO.setGstIn(sellerBuyer.getGstIn());
        sellerBuyerDTO.setCompanyLocation(sellerBuyer.getCompanyLocation());
        sellerBuyerDTO.setProfilePicture(null); // Set appropriately during file upload handling
        sellerBuyerDTO.setStorageLicenseFile(null); // Set appropriately during file upload handling
        sellerBuyerDTO.setCompanyCountry(sellerBuyer.getCompanyCountry());
        sellerBuyerDTO.setCompanyState(sellerBuyer.getCompanyState());
        sellerBuyerDTO.setCompanyCity(sellerBuyer.getCompanyCity());
        sellerBuyerDTO.setCompanyPinCode(sellerBuyer.getCompanyPinCode());
        sellerBuyerDTO.setWarehouseCountry(sellerBuyer.getWarehouseCountry());
        sellerBuyerDTO.setWarehouseState(sellerBuyer.getWarehouseState());
        sellerBuyerDTO.setWarehouseCity(sellerBuyer.getWarehouseCity());
        sellerBuyerDTO.setWarehousePinCode(sellerBuyer.getWarehousePinCode());
        sellerBuyerDTO.setWaiveSampleFree(sellerBuyer.getWaiveSampleFree());

//        sellerBuyerDTO.setProfilePictureUrl(s3Service.getUserImagePresignedUrl(sellerBuyer.getProfilePictureUrl()));
//        sellerBuyerDTO.setCertificateUrl(s3Service.getUserCertificatePresignedUrl(sellerBuyer.getCertificateUrl()));
//        sellerBuyerDTO.setCancelledChequeUrl(s3Service.getUserCancelledChequePresignedUrl(sellerBuyer.getCancelledChequeUrl()));


        sellerBuyerDTO.setProfilePictureUrl(
                Optional.ofNullable(sellerBuyer.getProfilePictureUrl())
                        .map(s3Service::getUserImagePresignedUrl)
                        .orElse(null)
        );

        sellerBuyerDTO.setCertificateUrl(
                Optional.ofNullable(sellerBuyer.getCertificateUrl())
                        .map(s3Service::getUserCertificatePresignedUrl)
                        .orElse(null)
        );

        sellerBuyerDTO.setCancelledChequeUrl(
                Optional.ofNullable(sellerBuyer.getCancelledChequeUrl())
                        .map(s3Service::getUserCancelledChequePresignedUrl)
                        .orElse(null)
        );

        return sellerBuyerDTO;
    }





    @Override
    // Method to check if the phone number exists in the cache
    public boolean isPhoneNumberInCache(String phoneNumber) {
        return sellerBuyerPhoneNumberCache.containsKey(phoneNumber);
    }
    @Override
    // Method to check if the email exists in the cache
    public boolean isEmailInCache(String email) {
        return sellerBuyerEmailCache.containsKey(email);
    }

    @Override
    public List<SellerBuyerDTO> allSellerBuyers() {
        List<SellerBuyer> sellerBuyers = sellerBuyerRepository.findAll();

        if (sellerBuyers == null || sellerBuyers.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if null or empty
        }

        return sellerBuyers.stream()
                .map(this::convertToSellerBuyerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SellerBuyerDTO findSellerBuyerById(String uID) {
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(uID).orElseThrow(() ->
                new EntityNotFoundException("SellerBuyer not found with ID: " + uID));
        return convertToSellerBuyerDTO(sellerBuyer);
    }



    // Method to add a phone number to the cache
    public void addToPhoneNumberCache(String phoneNumber, SellerBuyer sellerBuyer) {
        sellerBuyerPhoneNumberCache.put(phoneNumber, sellerBuyer);
    }

    public void addToPhoneEmailCache(String Email, SellerBuyer sellerBuyer) {
        sellerBuyerEmailCache.put(Email, sellerBuyer);
    }

    // Method to remove a phone number from the cache (if needed)
    public void removeFromPhoneNumberCache(String phoneNumber) {
        sellerBuyerPhoneNumberCache.remove(phoneNumber);
    }
    public void removeFromEmailCache(String email) {
        sellerBuyerEmailCache.remove(email);
    }



    //Buyer favorite products and category-----------------------------------------------------------------

    //
    public FavouriteProduct addFavouriteProduct(String userId, Long productId) {
        FavouriteProduct favouriteProduct = new FavouriteProduct();
        favouriteProduct.setUserId(userId);
        favouriteProduct.setProductId(productId);
        favouriteProduct.setAddedAt(LocalDateTime.now());
        return favouriteProductRepository.save(favouriteProduct);
    }

    public List<FavouriteProduct> getFavouriteProductsByUser(String userId) {
        return favouriteProductRepository.findByUserId(userId);
    }

    public List<FavouriteProduct> getFavouriteProductsByProduct(Long productId) {
        return favouriteProductRepository.findByProductId(productId);
    }

    public void removeFavouriteProduct(Long favProductId) {
        favouriteProductRepository.deleteById(favProductId);
    }

    public Optional<FavouriteProduct> getFavouriteProductById(Long favProductId) {
        return favouriteProductRepository.findById(favProductId);
    }



    //Favorite Category
    public FavouriteCategory addFavouriteCategory(String userId, Long categoryId) {
        FavouriteCategory favouriteCategory = new FavouriteCategory();
        favouriteCategory.setUserId(userId);
        favouriteCategory.setCategoryId(categoryId);
        favouriteCategory.setAddedAt(LocalDateTime.now());
        return favouriteCategoryRepository.save(favouriteCategory);
    }

    public List<FavouriteCategory> getFavouriteCategoriesByUser(String userId) {
        return favouriteCategoryRepository.findByUserId(userId);
    }

    @Override
    public void becomeBuyer(String userId) {
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(userId).orElse(null);
        if (sellerBuyer == null) {
            throw new RuntimeException("SellerBuyer not found");
        }
        sellerBuyer.setBuyer(true);
        sellerBuyerRepository.save(sellerBuyer);

        UserInfo userInfo = userInfoRepository.findByEmail(sellerBuyer.getEmail()).orElse(null);
        if (userInfo == null) {
            throw new RuntimeException("UserInfo not found");
        }

        if (!userInfo.getRoles().contains("ROLE_BUYER")) {
            userInfo.setRoles(userInfo.getRoles() + "," + "ROLE_BUYER");
            userInfoRepository.save(userInfo);
        }

    }

    @Override
    public void becomeSeller(String userId) {
        // Find the SellerBuyer entity by userId
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(userId).orElse(null);
        if (sellerBuyer == null) {
            throw new RuntimeException("SellerBuyer not found");
        }

        // Set the user as a seller
        sellerBuyer.setSeller(true);
        sellerBuyerRepository.save(sellerBuyer);

        // Find the UserInfo entity by email (from SellerBuyer)
        UserInfo userInfo = userInfoRepository.findByEmail(sellerBuyer.getEmail()).orElse(null);
        if (userInfo == null) {
            throw new RuntimeException("UserInfo not found");
        }

        // Append ROLE_SELLER to the user's roles if not already present
        if (!userInfo.getRoles().contains("ROLE_SELLER")) {
            userInfo.setRoles(userInfo.getRoles() + "," + "ROLE_SELLER");
            userInfoRepository.save(userInfo);
        }
    }



    public List<FavouriteCategory> getFavouriteCategoriesByCategory(Long categoryId) {
        return favouriteCategoryRepository.findByCategoryId(categoryId);
    }

    public void removeFavouriteCategory(Long favCategoryId) {
        favouriteCategoryRepository.deleteById(favCategoryId);
    }

    public Optional<FavouriteCategory> getFavouriteCategoryById(Long favCategoryId) {
        return favouriteCategoryRepository.findById(favCategoryId);
    }






    //Seller-Product -------------------------------------------------

@Override
    public SellerProduct addSellerProduct(SellerProductDTO sellerProductDTO) throws Exception {

        //Genetate SpId
        sellerProductDTO.setSpId(generateUniqueSpId());
    System.out.println(sellerProductDTO);
        SellerProduct sellerProduct = toEntity(sellerProductDTO);
        sellerProduct.setAddDate(LocalDate.now());
        sellerProduct.setAddTime(LocalTime.now());
        return sellerProductRepository.save(sellerProduct);

    }
    @Override
    public List<SellerProduct> getSellerProductsBySeller(String sellerId) {
        return sellerProductRepository.findBySellerId(sellerId);
    }
@Override
    public List<SellerProductDTO> getSellerProductDTOsBySeller(String sellerId) {
        // Fetch list of SellerProduct entities by sellerId
        List<SellerProduct> sellerProducts = sellerProductRepository.findBySellerId(sellerId);
        return sellerProducts.stream()
                .map(this::toDTO)  // Map each SellerProduct to SellerProductDTO
                .collect(Collectors.toList());
    }


    public List<SellerProduct> getSellerProductsByProduct(Long pId) {
        return sellerProductRepository.findByPId(pId);
    }

    public void removeSellerProduct(String spId) {
        sellerProductRepository.deleteById(spId);
    }
    @Override
    public Optional<SellerProduct> getSellerProductById(String spId) {
        return sellerProductRepository.findById(spId);
    }

    @Override
    public List<String> allEmail() {
        return sellerBuyerRepository.findAll().stream()
                .map(SellerBuyer::getEmail)
                .toList();
    }


    // Method to generate a unique spId
    private  String generateUniqueSpId() {
        return "SP-" + UUID.randomUUID().toString();
    }
    private SellerProduct toEntity(SellerProductDTO dto) throws Exception {
        SellerProduct sellerProduct = new SellerProduct();

        // Set fields from SellerProductDTO to SellerProduct
        sellerProduct.setSpId(dto.getSpId());
        sellerProduct.setSellerId(dto.getSellerId());
        sellerProduct.setMaxPricePerTon(dto.getMaxPricePerTon());
        sellerProduct.setMinPricePerTon(dto.getMinPricePerTon());
        sellerProduct.setDeliveryCharges(dto.getDeliveryCharges());
        sellerProduct.setDescription(dto.getDescription());
        sellerProduct.setGrainSize(dto.getGrainSize());
        sellerProduct.setAdmixing(dto.getAdmixing());
        sellerProduct.setMoisture(dto.getMoisture());
        sellerProduct.setOrigin(dto.getOrigin());
        sellerProduct.setDd(dto.getDd());
        sellerProduct.setKettValue(dto.getKettValue());
        sellerProduct.setChalky(dto.getChalky());
        sellerProduct.setForeignMaterial(dto.getForeignMaterial());
        sellerProduct.setWarehouse(dto.getWarehouse());
        sellerProduct.setAvailableAmount(dto.getAvailableAmount());
        sellerProduct.setProductImageUrl1(dto.getProductImageUrl1());
        sellerProduct.setProductImageUrl2(dto.getProductImageUrl2());
        sellerProduct.setProductCertificationUrl(dto.getProductCertificationUrl());
        sellerProduct.setAddDate(dto.getAddDate());
        sellerProduct.setAddTime(dto.getAddTime());
        sellerProduct.setEarliestAvailableDate(dto.getEarliestAvailableDate());

        // Set product ID and certification fields
        sellerProduct.setProductId(dto.getProductId());
        sellerProduct.setNpop(dto.getNpop());
        sellerProduct.setNop(dto.getNop());
        sellerProduct.setEu(dto.getEu());
        sellerProduct.setGsdc(dto.getGsdc());
        sellerProduct.setIpm(dto.getIpm());
        sellerProduct.setOther(dto.getOther());

        return sellerProduct;
    }


    private SellerProductDTO toDTO(SellerProduct sellerProduct) {
        SellerProductDTO dto = new SellerProductDTO();

        // Fetch product details safely with Optional
        Product product = productRepository.findById(sellerProduct.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Set fields from SellerProduct to SellerProductDTO
        dto.setSpId(sellerProduct.getSpId());
        dto.setSellerId(sellerProduct.getSellerId());
        dto.setMaxPricePerTon(sellerProduct.getMaxPricePerTon());
        dto.setMinPricePerTon(sellerProduct.getMinPricePerTon());
        dto.setDeliveryCharges(sellerProduct.getDeliveryCharges());
        dto.setDescription(sellerProduct.getDescription());
        dto.setGrainSize(sellerProduct.getGrainSize());
        dto.setAdmixing(sellerProduct.getAdmixing());
        dto.setMoisture(sellerProduct.getMoisture());
        dto.setOrigin(sellerProduct.getOrigin());
        dto.setDd(sellerProduct.getDd());
        dto.setKettValue(sellerProduct.getKettValue());
        dto.setChalky(sellerProduct.getChalky());
        dto.setForeignMaterial(sellerProduct.getForeignMaterial());
        dto.setWarehouse(sellerProduct.getWarehouse());
        dto.setAvailableAmount(sellerProduct.getAvailableAmount());
        dto.setProductImageUrl1(sellerProduct.getProductImageUrl1());
        dto.setProductImageUrl2(sellerProduct.getProductImageUrl2());
        dto.setProductCertificationUrl(sellerProduct.getProductCertificationUrl());
        dto.setAddDate(sellerProduct.getAddDate());
        dto.setAddTime(sellerProduct.getAddTime());
        dto.setEarliestAvailableDate(sellerProduct.getEarliestAvailableDate());

        // Set fields related to the Product entity
        dto.setProductId(sellerProduct.getProductId());
        dto.setProductName(product.getProductName());
        dto.setProductCategory(product.getCategory().getCategoryName());

        // Set certifications
        dto.setNpop(sellerProduct.getNpop());
        dto.setNop(sellerProduct.getNop());
        dto.setEu(sellerProduct.getEu());
        dto.setGsdc(sellerProduct.getGsdc());
        dto.setIpm(sellerProduct.getIpm());
        dto.setOther(sellerProduct.getOther());
        return dto;
    }





}
