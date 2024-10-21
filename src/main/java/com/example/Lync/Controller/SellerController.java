package com.example.Lync.Controller;

import com.example.Lync.Config.JwtService;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.DTO.SellerProductImageDto;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Entity.SellerProduct;
import com.example.Lync.Repository.SellerBuyerRepository;
import com.example.Lync.Repository.UserInfoRepository;
import com.example.Lync.Service.OTPStorageService;
import com.example.Lync.Service.OtpService;
import com.example.Lync.Service.SellerBuyerService;
import com.example.Lync.ServiceImpl.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://lync-reactjs-bucket.s3-website.ap-south-1.amazonaws.com", "https://another-domain.com"})

@RestController
@RequestMapping("/auth/seller")
public class SellerController {

    private final S3Service s3Service;
    private final UserInfoRepository repository;
    private final SellerBuyerRepository sellerBuyerRepository;
    private final UserInfoService service;
    private final JwtService jwtService;
    private final SellerBuyerService sellerBuyerService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final OTPStorageService otpStorageService;
    private final UserInfoService userInfoService;

    public SellerController(S3Service s3Service, UserInfoRepository repository, SellerBuyerRepository sellerBuyerRepository, UserInfoService service, JwtService jwtService, SellerBuyerService sellerBuyerService, AuthenticationManager authenticationManager, OtpService otpService, OTPStorageService otpStorageService, UserInfoService userInfoService) {
        this.s3Service = s3Service;
        this.repository = repository;
        this.sellerBuyerRepository = sellerBuyerRepository;
        this.service = service;
        this.jwtService = jwtService;
        this.sellerBuyerService = sellerBuyerService;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
        this.otpStorageService = otpStorageService;
        this.userInfoService = userInfoService;
    }

    @GetMapping("/sellerProfile")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public String sellerProfile() {
        return "Welcome to Seller Profile";
    }

    @GetMapping("/details")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<SellerBuyer> sellerDetails() {
        // Get the Authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the username from the Authentication object
        String username = authentication.getName();

        // Fetch SellerBuyer details based on email (username)
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        // Return the seller details wrapped in ResponseEntity
        return ResponseEntity.ok(sellerDetails);
    }


    // API to allow a seller to become a buyer
    @PostMapping("/becomeBuyer/{userId}")
    public ResponseEntity<String> becomeBuyer(@PathVariable String userId) {
        try {
            sellerBuyerService.becomeBuyer(userId);  // Call the service method
            return ResponseEntity.ok("User role updated to include ROLE_BUYER");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // Handle exceptions
        }
    }

    @PostMapping("/addSellerProduct")
    public ResponseEntity<SellerProduct> addSellerProduct(@RequestBody SellerProductDTO sellerProductDto) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the username from the Authentication object
        String username = authentication.getName();

        // Fetch SellerBuyer details based on email (username)
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        sellerProductDto.setSellerId(sellerDetails.getUserId());

        SellerProduct addedSellerProduct = sellerBuyerService.addSellerProduct(sellerProductDto);

        return ResponseEntity.ok(addedSellerProduct); // Returns the saved SellerProduct with 200 OK
    }

    @GetMapping("/sellerProduct")
    public ResponseEntity<List<SellerProductDTO>> getSellerProductsBySeller() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the username from the Authentication object
        String username = authentication.getName();

        // Fetch SellerBuyer details based on email (username)
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        List<SellerProductDTO> sellerProductDTOList = sellerBuyerService.getSellerProductDTOsBySeller(sellerDetails.getUserId());
        return ResponseEntity.ok(sellerProductDTOList); // Return the list of products with HTTP 200 OK
    }


}
