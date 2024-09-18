package com.example.Lync.ServiceImpl;

import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Entity.UserInfo;
import com.example.Lync.Repository.SellerBuyerRepository;
import com.example.Lync.Repository.UserInfoRepository;
import com.example.Lync.Service.SellerBuyerService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SellerBuyerServiceImpl implements SellerBuyerService {

    private  final SellerBuyerRepository sellerBuyerRepository;

    private final UserInfoRepository userInfoRepository;

    @Autowired
    @Lazy
    private PasswordEncoder encoder;


    public SellerBuyerServiceImpl(SellerBuyerRepository sellerBuyerRepository, UserInfoRepository userInfoRepository) {
        this.sellerBuyerRepository = sellerBuyerRepository;
        this.userInfoRepository = userInfoRepository;
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
    public void createSeller(SellerBuyerDTO sellerBuyerDTO) {
        sellerBuyerDTO.setBuyer(false);
        sellerBuyerDTO.setSeller(true);
        sellerBuyerDTO.setUserId(generateUserId());

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


    }

    @Override
    public void createBuyer(SellerBuyerDTO sellerBuyerDTO) {
        sellerBuyerDTO.setBuyer(true);
        sellerBuyerDTO.setSeller(false);
        sellerBuyerDTO.setUserId(generateUserId());

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


    private SellerBuyer convertToSellerBuyer(SellerBuyerDTO sellerBuyerDTO) {
        SellerBuyer sellerBuyer = new SellerBuyer();

        // Set basic fields
        sellerBuyer.setUserId(sellerBuyerDTO.getUserId());
        sellerBuyer.setFullName(sellerBuyerDTO.getFullName());
        sellerBuyer.setEmail(sellerBuyerDTO.getEmail());
        sellerBuyer.setPhoneNumber(sellerBuyerDTO.getPhoneNumber());
        sellerBuyer.setCountry(sellerBuyerDTO.getCountry());
        sellerBuyer.setState(sellerBuyerDTO.getState());
        sellerBuyer.setCity(sellerBuyerDTO.getCity());
        sellerBuyer.setPinCode(sellerBuyerDTO.getPinCode());
        sellerBuyer.setAddress(sellerBuyerDTO.getAddress());


        sellerBuyer.setSeller(sellerBuyerDTO.getSeller());
        sellerBuyer.setBuyer(sellerBuyerDTO.getBuyer());


        sellerBuyer.setIncorporationDate(sellerBuyerDTO.getIncorporationDate());
        sellerBuyer.setCompanyName(sellerBuyerDTO.getCompanyName());
        sellerBuyer.setGstIn(sellerBuyerDTO.getGstIn());
        sellerBuyer.setCompanyLocation(sellerBuyerDTO.getCompanyLocation());
//        sellerBuyer.setWareHouseAddress(sellerBuyerDTO.getWareHouseAddress());
//        sellerBuyer.setStorageLicense(sellerBuyerDTO.getStorageLicense());



        return sellerBuyer;
    }

    private SellerBuyerDTO convertToSellerBuyerDTO(SellerBuyer sellerBuyer) {
        SellerBuyerDTO sellerBuyerDTO = new SellerBuyerDTO();

        sellerBuyerDTO.setUserId(sellerBuyer.getUserId());
        sellerBuyerDTO.setFullName(sellerBuyer.getFullName());
        sellerBuyerDTO.setEmail(sellerBuyer.getEmail());
        sellerBuyerDTO.setPassword(sellerBuyer.getPassword());
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
        sellerBuyerDTO.setIncorporationDate(sellerBuyer.getIncorporationDate());
        sellerBuyerDTO.setCompanyName(sellerBuyer.getCompanyName());
        sellerBuyerDTO.setGstIn(sellerBuyer.getGstIn());
        sellerBuyerDTO.setCompanyLocation(sellerBuyer.getCompanyLocation());
//        sellerBuyerDTO.setWareHouseAddress(sellerBuyer.getWareHouseAddress());
//        sellerBuyerDTO.setStorageLicense(sellerBuyer.getStorageLicense());
//        sellerBuyerDTO.setProfilePictureUrl(sellerBuyer.getProfilePictureUrl());
//        sellerBuyerDTO.setStorageLicenseFileUrl(sellerBuyer.getStorageLicenseFileUrl());

        // Since you are using MultipartFile, you'll need to handle it accordingly.
        // You can map MultipartFile to null or handle the conversion separately.
        sellerBuyerDTO.setProfilePicture(null); // You can set this when handling the file upload
        sellerBuyerDTO.setStorageLicenseFile(null); // Same as above

        return sellerBuyerDTO;
    }




    @Override
    // Method to check if the phone number exists in the cache
    public boolean isPhoneNumberInCache(String phoneNumber) {
        return sellerBuyerPhoneNumberCache.containsKey(phoneNumber);
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

}
