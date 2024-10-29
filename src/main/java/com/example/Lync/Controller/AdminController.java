package com.example.Lync.Controller;

import com.example.Lync.Config.JwtService;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.DTO.UserInfoDTO;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Entity.UserInfo;
import com.example.Lync.Repository.SellerBuyerRepository;
import com.example.Lync.Repository.UserInfoRepository;
import com.example.Lync.Service.InquiryService;
import com.example.Lync.Service.OTPStorageService;
import com.example.Lync.Service.OtpService;
import com.example.Lync.Service.SellerBuyerService;
import com.example.Lync.ServiceImpl.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = {"http://localhost:5173", "http://lync-reactjs-bucket.s3-website.ap-south-1.amazonaws.com", "https://another-domain.com"})
@RestController
@RequestMapping("/auth/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")

public class AdminController {
    private final S3Service s3Service;
    private final UserInfoService service;
    private final JwtService jwtService;
    private final SellerBuyerService sellerBuyerService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final OTPStorageService otpStorageService;
    private final InquiryService inquiryService;

    // Removed the duplicate UserInfoService field
    public AdminController(S3Service s3Service,
                           UserInfoRepository repository,
                           SellerBuyerRepository sellerBuyerRepository,
                           UserInfoService service, // Keep only one UserInfoService
                           JwtService jwtService,
                           SellerBuyerService sellerBuyerService,
                           AuthenticationManager authenticationManager,
                           OtpService otpService,
                           OTPStorageService otpStorageService, InquiryService inquiryService) {
        this.s3Service = s3Service;
        this.service = service;
        this.jwtService = jwtService;
        this.sellerBuyerService = sellerBuyerService;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
        this.otpStorageService = otpStorageService;
        this.inquiryService = inquiryService;
    }


    @GetMapping("/adminProfile")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }


    @GetMapping("/details")
    public ResponseEntity<UserInfoDTO> sellerDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        // Fetch user info based on email (username)
        UserInfoDTO userInfo = service.findByEmail(username);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<SellerBuyerDTO>> allSellerBuyers() {
        List<SellerBuyerDTO> sellerBuyers = sellerBuyerService.allSellerBuyers();
        return ResponseEntity.ok(sellerBuyers);
    }

    @GetMapping("/userById/{uId}")
    public ResponseEntity<SellerBuyerDTO> getSellerBuyerById(@PathVariable String uId) {
        SellerBuyerDTO sellerBuyer = sellerBuyerService.findSellerBuyerById(uId);
        return ResponseEntity.ok(sellerBuyer);
    }

    @PutMapping("/editUser/{userId}")
    public ResponseEntity<String> editUser(@PathVariable String userId, @RequestBody SellerBuyerDTO sellerBuyerDTO){
        sellerBuyerService.editSellerBuyer(userId, sellerBuyerDTO);
        return ResponseEntity.ok("User Edited Successfully");
    }






    @GetMapping("/allInquiries")
    public ResponseEntity<?> getInquiries(){
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    @GetMapping("/getInquiryById/{qId}")
    public ResponseEntity<?> getInquiryById(@PathVariable String qId) throws Exception {
        return ResponseEntity.ok(inquiryService.getInquiryByQId(qId));
    }

    @PostMapping("/sendInquiryToSeller/{qId}")
    public ResponseEntity<String> sendInquiry(@PathVariable String qId, @RequestBody InquiryDTO inquiryDTO){
        inquiryService.sendInquiryToSeller(qId, inquiryDTO);
        return ResponseEntity.ok("Sent Inquiry to Seller");
    }

    @GetMapping("/sellerSellingProduct/{productId}")
    public ResponseEntity<List<SellerProductDTO>> sellersSellingProducts(@PathVariable Long productId){
        return ResponseEntity.ok(inquiryService.sellersSellingProduct(productId));
    }


}