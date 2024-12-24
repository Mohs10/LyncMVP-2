package com.example.Lync.ServiceImpl;

import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.*;
import com.example.Lync.Entity.*;

import com.example.Lync.Repository.*;
import com.example.Lync.Service.SellerBuyerService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class SellerBuyerServiceImpl implements SellerBuyerService {

    private  final SellerBuyerRepository sellerBuyerRepository;

    private final UserInfoRepository userInfoRepository;

    private final FavouriteCategoryRepository favouriteCategoryRepository;

    private final TypeRepository typeRepository;
    private final ProductRepository productRepository;
    private final FavouriteProductRepository favouriteProductRepository;
    private final SellerProductRepository sellerProductRepository;

    private final FormRepository formRepository;
    private final VarietyRepository varietyRepository;
    private final SellerProductSpecificationRepository sellerProductSpecificationRepository;
    private final S3Service s3Service;
    private final SellerBuyerAddressRepository sellerBuyerAddressRepository;

    private static final AtomicInteger serialNumber = new AtomicInteger(1); // Initialize starting value

    @Autowired
    @Lazy
    private PasswordEncoder encoder;


    public SellerBuyerServiceImpl(SellerBuyerRepository sellerBuyerRepository, UserInfoRepository userInfoRepository, FavouriteCategoryRepository favouriteCategoryRepository, TypeRepository typeRepository, ProductRepository productRepository, FavouriteProductRepository favouriteProductRepository, SellerProductRepository sellerProductRepository, FormRepository formRepository, VarietyRepository varietyRepository, SellerProductSpecificationRepository sellerProductSpecificationRepository, S3Service s3Service, SellerBuyerAddressRepository sellerBuyerAddressRepository) {
        this.sellerBuyerRepository = sellerBuyerRepository;
        this.userInfoRepository = userInfoRepository;
        this.favouriteCategoryRepository = favouriteCategoryRepository;
        this.typeRepository = typeRepository;
        this.productRepository = productRepository;
        this.favouriteProductRepository = favouriteProductRepository;
        this.sellerProductRepository = sellerProductRepository;
        this.formRepository = formRepository;
        this.varietyRepository = varietyRepository;
        this.sellerProductSpecificationRepository = sellerProductSpecificationRepository;
        this.s3Service = s3Service;
        this.sellerBuyerAddressRepository = sellerBuyerAddressRepository;
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
//        if (sellerBuyerDTO.getProfilePicture() != null) {
//            String profilePictureUrl =s3Service. uploadUserImage(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getProfilePicture());
//            sellerBuyerDTO.setProfilePictureUrl(profilePictureUrl);
//        }
//
//        // Upload certificate if provided
//        if (sellerBuyerDTO.getCertificate() != null) {
//            String certificateUrl = s3Service.uploadUserCertificate(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getCertificate());
//            sellerBuyerDTO.setCertificateUrl(certificateUrl);
//        }
//
//        // Upload cancelled cheque if provided
//        if (sellerBuyerDTO.getCancelledCheque() != null) {
//            String cancelledChequeUrl = s3Service.uploadUserCancelledCheque(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getCancelledCheque());
//            sellerBuyerDTO.setCancelledChequeUrl(cancelledChequeUrl);
//        }



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
//        if (sellerBuyerDTO.getProfilePicture() != null) {
//            String profilePictureUrl =s3Service. uploadUserImage(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getProfilePicture());
//            sellerBuyerDTO.setProfilePictureUrl(profilePictureUrl);
//        }
//
//        // Upload certificate if provided
//        if (sellerBuyerDTO.getCertificate() != null) {
//            String certificateUrl = s3Service.uploadUserCertificate(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getCertificate());
//            sellerBuyerDTO.setCertificateUrl(certificateUrl);
//        }
//
//        // Upload cancelled cheque if provided
//        if (sellerBuyerDTO.getCancelledCheque() != null) {
//            String cancelledChequeUrl = s3Service.uploadUserCancelledCheque(sellerBuyerDTO.getUserId(), sellerBuyerDTO.getCancelledCheque());
//            sellerBuyerDTO.setCancelledChequeUrl(cancelledChequeUrl);
//        }


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
//        sellerBuyer.setSeller(sellerBuyerDTO.getSeller());
//        sellerBuyer.setBuyer(sellerBuyerDTO.getBuyer());
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


    // Method to upload profile picture
    public String uploadProfilePicture(String userId, MultipartFile profilePicture) throws IOException {
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(userId).orElseThrow(()->
                new RuntimeException("User not found with User ID: " + userId));
        if (profilePicture != null) {
            String profilePictureUrl = s3Service.uploadUserImage(userId, profilePicture);
            sellerBuyer.setProfilePictureUrl(profilePictureUrl); // Save URL in the SellerBuyer entity if needed
            sellerBuyerRepository.save(sellerBuyer); // Persist changes
            return profilePictureUrl;
        }
        return null;
    }

    // Method to upload certificate
    public String uploadCertificate(String userId, MultipartFile certificate) throws IOException {
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(userId).orElseThrow(()->
                new RuntimeException("User not found with User ID: " + userId));
        if (certificate != null) {
            String certificateUrl = s3Service.uploadUserCertificate(userId, certificate);
            sellerBuyer.setCertificateUrl(certificateUrl); // Save URL in the SellerBuyer entity if needed
            sellerBuyerRepository.save(sellerBuyer); // Persist changes
            return certificateUrl;
        }
        return null;
    }

    // Method to upload cancelled cheque
    public String uploadCancelledCheque(String userId, MultipartFile cancelledCheque) throws IOException {
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(userId).orElseThrow(()->
                new RuntimeException("User not found with User ID: " + userId));
        if (cancelledCheque != null) {
            String cancelledChequeUrl = s3Service.uploadUserCancelledCheque(userId, cancelledCheque);
            sellerBuyer.setCancelledChequeUrl(cancelledChequeUrl); // Save URL in the SellerBuyer entity if needed
            sellerBuyerRepository.save(sellerBuyer); // Persist changes
            return cancelledChequeUrl;
        }
        return null;
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


    public SellerBuyerDTO convertToSellerBuyerDTO(SellerBuyer sellerBuyer) {
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
//        sellerBuyerDTO.setProfilePicture(null); // Set appropriately during file upload handling
//        sellerBuyerDTO.setStorageLicenseFile(null); // Set appropriately during file upload handling
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


        List<SellerProductSpecification> specifications=new ArrayList<>();
    for (SpecificationDTO specificationDTO : sellerProductDTO.getSpecifications()) {
        SellerProductSpecification specification = new SellerProductSpecification();
        specification.setSpecificationName(specificationDTO.getSpecificationName());
        specification.setSpecificationValue(specificationDTO.getSpecificationValue());
        specification.setSpecificationValueUnits(specificationDTO.getSpecificationValueUnits());
        specifications.add(sellerProductSpecificationRepository.save(specification));
    }

    //Genetate SpId
    sellerProductDTO.setSpId(generateUniqueSpId());
    SellerProduct sellerProduct = new SellerProduct();

    // Map DTO fields to the entity
    sellerProduct.setSpId(sellerProductDTO.getSpId());
    sellerProduct.setSellerId(sellerProductDTO.getSellerId());
    sellerProduct.setProductId(sellerProductDTO.getProductId());
    sellerProduct.setProductFormId(sellerProductDTO.getProductFormId());
    sellerProduct.setProductVarietyId(sellerProductDTO.getProductVarietyId());
    sellerProduct.setOriginOfProduce(sellerProductDTO.getOriginOfProduce());
    sellerProduct.setAvailableAmount(sellerProductDTO.getAvailableAmount());
    sellerProduct.setUnit(sellerProductDTO.getUnit());
    sellerProduct.setDescription(sellerProductDTO.getDescription());
    sellerProduct.setMaxPrice(sellerProductDTO.getMaxPrice());
    sellerProduct.setMinPrice(sellerProductDTO.getMinPrice());
    sellerProduct.setDeliveryCharges(sellerProductDTO.getDeliveryCharges());
    sellerProduct.setPriceTerms(sellerProductDTO.getPriceTerms());
    sellerProduct.setPackagingMaterial(sellerProductDTO.getPackagingMaterial());
    sellerProduct.setPaymentTerms(sellerProductDTO.getPaymentTerms());
    sellerProduct.setEarliestAvailableDate(sellerProductDTO.getEarliestAvailableDate());
    sellerProduct.setWarehouseCountry(sellerProductDTO.getWarehouseCountry());
    sellerProduct.setWarehouseState(sellerProductDTO.getWarehouseState());
    sellerProduct.setWarehouseCity(sellerProductDTO.getWarehouseCity());
    sellerProduct.setWarehousePinCode(sellerProductDTO.getWarehousePinCode());
    sellerProduct.setCertificationName(sellerProductDTO.getCertificationName());
    sellerProduct.setCertificationFileUrl(sellerProductDTO.getCertificationFileUrl());

//    // Handle images
//    if (sellerProductDTO.getProductImage1() != null && !sellerProductDTO.getProductImage1().isEmpty()) {
//       String image1Url =s3Service.uploadSellerProductImage1(sellerProductDTO.getSellerId(),sellerProductDTO.getSpId(),sellerProductDTO.getProductImage1());
//        sellerProduct.setProductImageUrl1(image1Url);
//    }
//    if (sellerProductDTO.getProductImage2() != null && !sellerProductDTO.getProductImage2().isEmpty()) {
//        String image2Url =s3Service.uploadSellerProductImage2(sellerProductDTO.getSellerId(),sellerProductDTO.getSpId(),sellerProductDTO.getProductImage2());
//        sellerProduct.setProductImageUrl2(image2Url);
//        // Upload image2 logic
//    }
//
//    // Handle certification file
//    if (sellerProductDTO.getCertificationFile() != null && !sellerProductDTO.getCertificationFile().isEmpty()) {
//        String certificateUrl = s3Service.uploadSellerProductCertificate(sellerProductDTO.getSellerId(),sellerProductDTO.getSpId(),sellerProductDTO.getCertificationFile());
//        sellerProduct.setCertificationFileUrl(certificateUrl);
//        // Upload certification file logic
//    }

    // Set Specifications (many-to-many relationship)
    if (sellerProductDTO.getSpecifications() != null) {
      sellerProduct.setSpecifications(specifications);
    }

    // Save or update SellerProduct
    return sellerProductRepository.save(sellerProduct);

    }


    public String uploadSellerProductImage1(String sellerProductId, MultipartFile productImage) throws IOException {
        SellerProduct sellerProduct = sellerProductRepository.findById(sellerProductId).orElseThrow(() -> new RuntimeException("Product not found"));


        if (productImage != null) {
            String productImageUrl = s3Service.uploadSellerProductImage1(sellerProduct.getSellerId(),sellerProduct.getSpId(),productImage);
            sellerProduct.setProductImageUrl1(productImageUrl); // Save URL in the SellerBuyer entity if needed
            sellerProductRepository.save(sellerProduct); // Persist changes
            return productImageUrl;
        }
        return null;
    }
    public String uploadSellerProductImage2(String sellerProductId, MultipartFile productImage) throws IOException {
        SellerProduct sellerProduct = sellerProductRepository.findById(sellerProductId).orElseThrow(() -> new RuntimeException("Product not found"));


        if (productImage != null) {
            String productImageUrl = s3Service.uploadSellerProductImage2(sellerProduct.getSellerId(),sellerProduct.getSpId(),productImage);
            sellerProduct.setProductImageUrl2(productImageUrl); // Save URL in the SellerBuyer entity if needed
            sellerProductRepository.save(sellerProduct); // Persist changes
            return productImageUrl;
        }
        return null;
    }
    public String uploadSellerProductCertificate(String sellerProductId, MultipartFile productCertificate) throws IOException {
        SellerProduct sellerProduct = sellerProductRepository.findById(sellerProductId).orElseThrow(() -> new RuntimeException("Product not found"));


        if (productCertificate != null) {
            String productImageUrl = s3Service.uploadSellerProductCertificate(sellerProduct.getSellerId(),sellerProduct.getSpId(),productCertificate);
            sellerProduct.setCertificationFileUrl(productImageUrl); // Save URL in the SellerBuyer entity if needed
            sellerProductRepository.save(sellerProduct); // Persist changes
            return productImageUrl;
        }
        return null;
    }


@Override
    public SellerProduct editSellerProduct(SellerProduct existingSellerProduct, SellerProductDTO sellerProductDTO) throws Exception {

        // Update specifications
        List<SellerProductSpecification> specifications = new ArrayList<>();
        for (SpecificationDTO specificationDTO : sellerProductDTO.getSpecifications()) {
            SellerProductSpecification specification = new SellerProductSpecification();
            specification.setSpecificationName(specificationDTO.getSpecificationName());
            specification.setSpecificationValue(specificationDTO.getSpecificationValue());
            specification.setSpecificationValueUnits(specificationDTO.getSpecificationValueUnits());
            specifications.add(sellerProductSpecificationRepository.save(specification));
        }

        // Update fields of the existing seller product
        existingSellerProduct.setProductId(sellerProductDTO.getProductId());
        existingSellerProduct.setProductFormId(sellerProductDTO.getProductFormId());
        existingSellerProduct.setProductVarietyId(sellerProductDTO.getProductVarietyId());
        existingSellerProduct.setOriginOfProduce(sellerProductDTO.getOriginOfProduce());
        existingSellerProduct.setAvailableAmount(sellerProductDTO.getAvailableAmount());
        existingSellerProduct.setUnit(sellerProductDTO.getUnit());
        existingSellerProduct.setDescription(sellerProductDTO.getDescription());
        existingSellerProduct.setMaxPrice(sellerProductDTO.getMaxPrice());
        existingSellerProduct.setMinPrice(sellerProductDTO.getMinPrice());
        existingSellerProduct.setDeliveryCharges(sellerProductDTO.getDeliveryCharges());
        existingSellerProduct.setPriceTerms(sellerProductDTO.getPriceTerms());
        existingSellerProduct.setPackagingMaterial(sellerProductDTO.getPackagingMaterial());
        existingSellerProduct.setPaymentTerms(sellerProductDTO.getPaymentTerms());
        existingSellerProduct.setEarliestAvailableDate(sellerProductDTO.getEarliestAvailableDate());
        existingSellerProduct.setWarehouseCountry(sellerProductDTO.getWarehouseCountry());
        existingSellerProduct.setWarehouseState(sellerProductDTO.getWarehouseState());
        existingSellerProduct.setWarehouseCity(sellerProductDTO.getWarehouseCity());
        existingSellerProduct.setWarehousePinCode(sellerProductDTO.getWarehousePinCode());
        existingSellerProduct.setCertificationName(sellerProductDTO.getCertificationName());

//        // Update certification file if a new one is provided
//        if (sellerProductDTO.getCertificationFile() != null && !sellerProductDTO.getCertificationFile().isEmpty()) {
//            String certificateUrl = s3Service.uploadSellerProductCertificate(sellerProductDTO.getSellerId(), sellerProductDTO.getSpId(), sellerProductDTO.getCertificationFile());
//            existingSellerProduct.setCertificationFileUrl(certificateUrl);
//        }
//
//        // Handle product images
//        if (sellerProductDTO.getProductImage1() != null && !sellerProductDTO.getProductImage1().isEmpty()) {
//            String image1Url = s3Service.uploadSellerProductImage1(sellerProductDTO.getSellerId(), sellerProductDTO.getSpId(), sellerProductDTO.getProductImage1());
//            existingSellerProduct.setProductImageUrl1(image1Url);
//        }
//        if (sellerProductDTO.getProductImage2() != null && !sellerProductDTO.getProductImage2().isEmpty()) {
//            String image2Url = s3Service.uploadSellerProductImage2(sellerProductDTO.getSellerId(), sellerProductDTO.getSpId(), sellerProductDTO.getProductImage2());
//            existingSellerProduct.setProductImageUrl2(image2Url);
//        }

        // Update specifications (many-to-many relationship)
        if (sellerProductDTO.getSpecifications() != null) {
            existingSellerProduct.setSpecifications(specifications);
        }

        // Save or update the existing SellerProduct
        return sellerProductRepository.save(existingSellerProduct);
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
    public static String generateUniqueSpId() {
        // Get the current year
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String year = dateTimeFormat.format(new Date());

        // Get the serial number and increment it atomically
        int serial = serialNumber.getAndIncrement();

        // Format the serial number with leading zeros
        String formattedSerial = String.format("%05d", serial);

        // Generate the sellerProductId
        return "SP-" + year + "-" + formattedSerial;
    }
    private SellerProduct toEntity(SellerProductDTO dto) throws Exception {
        SellerProduct sellerProduct = new SellerProduct();

        // Set fields from SellerProductDTO to SellerProduct
//        sellerProduct.setSpId(dto.getSpId());
//        sellerProduct.setSellerId(dto.getSellerId());
//        sellerProduct.setMaxPricePerTon(dto.getMaxPricePerTon());
//        sellerProduct.setMinPricePerTon(dto.getMinPricePerTon());
//        sellerProduct.setDeliveryCharges(dto.getDeliveryCharges());
//        sellerProduct.setDescription(dto.getDescription());
//        sellerProduct.setGrainSize(dto.getGrainSize());
//        sellerProduct.setAdmixing(dto.getAdmixing());
//        sellerProduct.setMoisture(dto.getMoisture());
//        sellerProduct.setOrigin(dto.getOrigin());
//        sellerProduct.setDd(dto.getDd());
//        sellerProduct.setKettValue(dto.getKettValue());
//        sellerProduct.setChalky(dto.getChalky());
//        sellerProduct.setForeignMaterial(dto.getForeignMaterial());
//        sellerProduct.setWarehouse(dto.getWarehouse());
//        sellerProduct.setAvailableAmount(dto.getAvailableAmount());
//        sellerProduct.setProductImageUrl1(dto.getProductImageUrl1());
//        sellerProduct.setProductImageUrl2(dto.getProductImageUrl2());
//        sellerProduct.setProductCertificationUrl(dto.getProductCertificationUrl());
//        sellerProduct.setAddDate(dto.getAddDate());
//        sellerProduct.setAddTime(dto.getAddTime());
//        sellerProduct.setEarliestAvailableDate(dto.getEarliestAvailableDate());
//
//        // Set product ID and certification fields
//        sellerProduct.setProductId(dto.getProductId());
//        sellerProduct.setNpop(dto.getNpop());
//        sellerProduct.setNop(dto.getNop());
//        sellerProduct.setEu(dto.getEu());
//        sellerProduct.setGsdc(dto.getGsdc());
//        sellerProduct.setIpm(dto.getIpm());
//        sellerProduct.setOther(dto.getOther());

        return sellerProduct;
    }




    public  SellerProductDTO toDTO(SellerProduct sellerProduct) {
        SellerProductDTO dto = new SellerProductDTO();

        dto.setSpId(sellerProduct.getSpId());
        dto.setSellerId(sellerProduct.getSellerId());
        dto.setProductId(sellerProduct.getProductId());

        // Product details
        dto.setProductFormId(sellerProduct.getProductFormId());
        dto.setProductVarietyId(sellerProduct.getProductVarietyId());

        String productName = productRepository.findById(sellerProduct.getProductId())
                .map(Product::getProductName)
                .orElse("Not found");

        String formName = formRepository.findById(sellerProduct.getProductFormId())
                .map(Form::getFormName)
                .orElse("Not found");

        String varietyName = varietyRepository.findById(sellerProduct.getProductVarietyId())
                .map(Variety::getVarietyName)
                .orElse("Not found");

        dto.setProductName(productName);
        dto.setProductFormName(formName);
        dto.setProductVarietyName(varietyName);



        dto.setOriginOfProduce(sellerProduct.getOriginOfProduce());
        dto.setAvailableAmount(sellerProduct.getAvailableAmount());
        dto.setUnit(sellerProduct.getUnit());
        dto.setDescription(sellerProduct.getDescription());

        String image1Url = (sellerProduct.getProductImageUrl1() != null) ?
                s3Service.getSellerProductImage1Url(sellerProduct.getProductImageUrl1()) : null;

        String image2Url = (sellerProduct.getProductImageUrl2() != null) ?
                s3Service.getSellerProductImage2Url(sellerProduct.getProductImageUrl2()) : null;

        String certificateUrl = (sellerProduct.getCertificationFileUrl() != null) ?
                s3Service.getSellerProductCertificateUrl(sellerProduct.getCertificationFileUrl()) : null;
        // Product Images
        dto.setProductImageUrl1(image1Url);
        dto.setProductImageUrl2(image2Url);

        // Pricing details
        dto.setMaxPrice(sellerProduct.getMaxPrice());
        dto.setMinPrice(sellerProduct.getMinPrice());
        dto.setDeliveryCharges(sellerProduct.getDeliveryCharges());
        dto.setPriceTerms(sellerProduct.getPriceTerms());

        // Packaging & Payment
        dto.setPackagingMaterial(sellerProduct.getPackagingMaterial());
        dto.setPaymentTerms(sellerProduct.getPaymentTerms());

        // Availability
        dto.setEarliestAvailableDate(sellerProduct.getEarliestAvailableDate());

        // Warehouse location
        dto.setWarehouseCountry(sellerProduct.getWarehouseCountry());
        dto.setWarehouseState(sellerProduct.getWarehouseState());
        dto.setWarehouseCity(sellerProduct.getWarehouseCity());
        dto.setWarehousePinCode(sellerProduct.getWarehousePinCode());

        // Certification details
        dto.setCertificationName(sellerProduct.getCertificationName());
        dto.setCertificationFileUrl(certificateUrl);

        // Specifications
        List<SpecificationDTO> specificationsDTO = sellerProduct.getSpecifications().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        dto.setSpecifications(specificationsDTO);

        return dto;
    }




    public PriceRangeProjection priceRangeByProductId(Long productId) {
        List<SellerProduct> sellerProducts = sellerProductRepository.findByPId(productId);

        if (sellerProducts.isEmpty()) {
            System.out.println("No products found for the given productId.");
            return null; // Return null if no products are found
        }

        PriceRangeProjection priceRangeProjection = new PriceRangeProjection();
        priceRangeProjection.setProductId(productId);

        Double minPrice = sellerProducts.stream()
                .mapToDouble(SellerProduct::getMinPrice)
                .min()
                .orElse(Double.MAX_VALUE);

        Double maxPrice = sellerProducts.stream()
                .mapToDouble(SellerProduct::getMaxPrice)
                .max()
                .orElse(Double.MIN_VALUE);

        priceRangeProjection.setMinPrice(minPrice);
        priceRangeProjection.setMaxPrice(maxPrice);

        return priceRangeProjection; // Return the computed PriceRangeProjection
    }




    private  SpecificationDTO toDTO(SellerProductSpecification specification)
    { SpecificationDTO specificationDTO = new SpecificationDTO();
        specificationDTO.setSpecificationName(specification.getSpecificationName());
        specificationDTO.setSpecificationValue(specification.getSpecificationValue());
        specificationDTO.setSpecificationValueUnits(specification.getSpecificationValueUnits());
        return  specificationDTO;

    }

    @Override
    public String enableWaiveSampleFree(String userId, Boolean enable) {
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("SellerBuyer not found with ID: " + userId));

        sellerBuyer.setWaiveSampleFree(enable);
        sellerBuyerRepository.save(sellerBuyer);

        return "Sample order fee has been waived off.";
    }

    @Override
    public String disableWaiveSampleFree(String userId, Boolean disable) {
        SellerBuyer sellerBuyer = sellerBuyerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("SellerBuyer not found with ID: " + userId));

        sellerBuyer.setWaiveSampleFree(disable);
        sellerBuyerRepository.save(sellerBuyer);

        return "Sample order fee has been added.";
    }

    @Override
    public String addAddress(String userId, SellerBuyerAddressDTO sellerBuyerAddressDTO) {
        SellerBuyerAddress sellerBuyerAddress = new SellerBuyerAddress();
        sellerBuyerAddress.setUId(userId);
        sellerBuyerAddress.setAddress(sellerBuyerAddressDTO.getAddress());
        sellerBuyerAddress.setCity(sellerBuyerAddressDTO.getCity());
        sellerBuyerAddress.setState(sellerBuyerAddressDTO.getState());
        sellerBuyerAddress.setCountry(sellerBuyerAddressDTO.getCountry());
        sellerBuyerAddress.setPincode(sellerBuyerAddressDTO.getPincode());
        sellerBuyerAddressRepository.save(sellerBuyerAddress);
        return "Address added successfully";
    }

    @Override
    public List<SellerBuyerAddressDTO> userGetsAddresses(String userId) {
        List<SellerBuyerAddress> sellerBuyerAddresses = sellerBuyerAddressRepository.findByUId(userId);
        List<SellerBuyerAddressDTO> sellerBuyerAddressDTOS = new ArrayList<>();
        for(SellerBuyerAddress sellerBuyerAddress : sellerBuyerAddresses){
            SellerBuyerAddressDTO dto = new SellerBuyerAddressDTO();
            dto.setUaId(sellerBuyerAddress.getUaId());
            dto.setUId(sellerBuyerAddress.getUId());
            dto.setAddress(sellerBuyerAddress.getAddress());
            dto.setCity(sellerBuyerAddress.getCity());
            dto.setCountry(sellerBuyerAddress.getCountry());
            dto.setPincode(sellerBuyerAddress.getPincode());
            dto.setState(sellerBuyerAddress.getState());
            sellerBuyerAddressDTOS.add(dto);
        }
        return sellerBuyerAddressDTOS;
    }

    @Override
    public SellerBuyerAddressDTO userGetAddressById(String userId, Long uaId) {
        SellerBuyerAddress sellerBuyerAddress = sellerBuyerAddressRepository.findById(uaId)
                .orElseThrow(() -> new RuntimeException("Address not found with Id : " + uaId));
        SellerBuyerAddressDTO dto = new SellerBuyerAddressDTO();
        dto.setUaId(sellerBuyerAddress.getUaId());
        dto.setUId(sellerBuyerAddress.getUId());
        dto.setAddress(sellerBuyerAddress.getAddress());
        dto.setCity(sellerBuyerAddress.getCity());
        dto.setState(sellerBuyerAddress.getState());
        dto.setCountry(sellerBuyerAddress.getCountry());
        dto.setPincode(sellerBuyerAddress.getPincode());
        return dto;
    }


}
