package com.example.Lync.Controller;


import com.example.Lync.Config.JwtService;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.AuthRequest;
import com.example.Lync.DTO.OTP_DTO;
import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Entity.UserInfo;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = {"http://localhost:5173", "http://lync-reactjs-bucket.s3-website.ap-south-1.amazonaws.com", "https://another-domain.com"})

@RestController
@RequestMapping("/auth")
public class UserController {

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




    public UserController(S3Service s3Service, UserInfoRepository repository, SellerBuyerRepository sellerBuyerRepository, UserInfoService service, JwtService jwtService, SellerBuyerService sellerBuyerService, AuthenticationManager authenticationManager, OtpService otpService, OTPStorageService otpStorageService, UserInfoService userInfoService) {
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

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    @GetMapping("/allEmails")
    public ResponseEntity<List<String>> allEmails(){
        return ResponseEntity.ok(sellerBuyerService.allEmail());
    }












    //S3 bucket upload ==================================================================================


    // API to upload user image
    @PostMapping("/api/{userId}/upload-image")
    public ResponseEntity<String> uploadUserImage(@PathVariable String userId, @RequestParam("file") MultipartFile file) {

        System.out.println("here");
        try {
            String s3Key = s3Service.uploadUserImage(userId, file);
            return ResponseEntity.ok("Image uploaded successfully to: " + s3Key);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    // API to upload user license
    @PostMapping("/api/{userId}/upload-license")
    public ResponseEntity<String> uploadUserLicense(@PathVariable String userId, @RequestParam("file") MultipartFile file) {
        try {
            String s3Key = s3Service.uploadUserLicense(userId, file);
            return ResponseEntity.ok("License uploaded successfully to: " + s3Key);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload license: " + e.getMessage());
        }
    }



    // API to retrieve user image as presigned URL
    @GetMapping("/api/{userId}/image")
    public ResponseEntity<String> getUserImage(@PathVariable String userId, @RequestParam("fileName") String fileName) {
        try {
            String presignedUrl = s3Service.getUserImagePresignedUrl(userId, fileName);
            return ResponseEntity.ok("Image download URL: " + presignedUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to retrieve image: " + e.getMessage());
        }
    }

    // API to retrieve user license as presigned URL
    @GetMapping("/api/{userId}/license")
    public ResponseEntity<String> getUserLicense(@PathVariable String userId, @RequestParam("fileName") String fileName) {
        try {
            String presignedUrl = s3Service.getUserLicensePresignedUrl(userId, fileName);
            return ResponseEntity.ok("License download URL: " + presignedUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to retrieve license: " + e.getMessage());
        }
    }








}

