package com.example.Lync.Controller;

import com.example.Lync.Config.JwtService;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.AuthRequest;
import com.example.Lync.DTO.OTP_DTO;
import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.Repository.SellerBuyerRepository;
import com.example.Lync.Repository.UserInfoRepository;
import com.example.Lync.Service.OTPStorageService;
import com.example.Lync.Service.OtpService;
import com.example.Lync.Service.SellerBuyerService;
import com.example.Lync.ServiceImpl.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@CrossOrigin(origins = {"http://localhost:5173", "http://lync-reactjs-bucket.s3-website.ap-south-1.amazonaws.com", "https://another-domain.com"})
@RestController
@RequestMapping("/api")
public class LoginController {



    private final UserInfoService service;
    private final JwtService jwtService;
    private final SellerBuyerService sellerBuyerService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final OTPStorageService otpStorageService;
    private final UserInfoService userInfoService;

    public LoginController(S3Service s3Service, UserInfoRepository repository, SellerBuyerRepository sellerBuyerRepository, UserInfoService service, JwtService jwtService, SellerBuyerService sellerBuyerService, AuthenticationManager authenticationManager, OtpService otpService, OTPStorageService otpStorageService, UserInfoService userInfoService) {

        this.service = service;
        this.jwtService = jwtService;
        this.sellerBuyerService = sellerBuyerService;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
        this.otpStorageService = otpStorageService;
        this.userInfoService = userInfoService;
    }



    //Seller/Buyer Register----------------------------------------------------------------
    @PostMapping("/addSeller")
    public ResponseEntity<String> addSeller(@RequestBody SellerBuyerDTO sellerBuyerDTO) {
        try {
            // Check if the phone number is already in cache
            if (sellerBuyerService.isPhoneNumberInCache(sellerBuyerDTO.getPhoneNumber())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already exists.");
            }

            // Check if the email is already in cache
            if (sellerBuyerService.isEmailInCache(sellerBuyerDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
            }

            // If both checks pass, create the seller
            sellerBuyerService.createSeller(sellerBuyerDTO);
            return ResponseEntity.ok("Seller added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while adding seller.");
        }
    }

    @PostMapping("/addBuyer")
    public ResponseEntity<String> addBuyer(@RequestBody SellerBuyerDTO sellerBuyerDTO) {
        try {
            // Check if the phone number is already in cache
            if (sellerBuyerService.isPhoneNumberInCache(sellerBuyerDTO.getPhoneNumber())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already exists.");
            }

            // Check if the email is already in cache
            if (sellerBuyerService.isEmailInCache(sellerBuyerDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
            }

            // If both checks pass, create the buyer
            sellerBuyerService.createBuyer(sellerBuyerDTO);
            return ResponseEntity.ok("Buyer added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while adding Buyer.");
        }
    }



    //login-----------------------------------------------
    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {

        System.out.println(authRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }


    // OTP ------------------------------------------------------------------------------------


    @PostMapping("/register/phone-number") // Number verification and OTP sending
    public ResponseEntity<Map<String, String>> registerNumber (@RequestBody OTP_DTO otpDetails) {


        Map<String, String> response = new HashMap<>();
        String phoneNumber = otpDetails.getPhoneNumber();
        Boolean isPresent = sellerBuyerService.isPhoneNumberInCache(phoneNumber);

        if (isPresent)
        {
            response.put("message", "Number already exists.");
            response.put("phoneNumber", phoneNumber);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        else
        {
            if (phoneNumber.matches("^[0-9]{10}$")) {
                String otp = otpService.generateRandomOTP();
                otpStorageService.storeOTP(phoneNumber, otp);
                otpService.sendOTP(phoneNumber, otp);
                response.put("message", "OTP generated");
                response.put("phoneNumber", phoneNumber);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "Invalid Number");
                response.put("phoneNumber", phoneNumber);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
    }

    @PostMapping("/verify/otp") // OTP verification
    public ResponseEntity<Map<String, String>> registerOTPVerify (@RequestBody OTP_DTO otpDetails) {

        String phoneNumber=otpDetails.getPhoneNumber();
        String enteredOTP=otpDetails.getEnteredOTP();

        Map<String, String> response = new HashMap<>();
        String storedOTP = otpStorageService.getStoredOTP(phoneNumber);


        if (storedOTP != null && enteredOTP.equals(storedOTP)) {
            otpStorageService.removeOTP(phoneNumber); // Remove the OTP after successful verification
            response.put("phoneNumber", phoneNumber);

            response.put("message", "Verified successfully.");

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } else {
            response.put("phoneNumber", phoneNumber);
            response.put("message", "Invalid OTP. Authentication failed.");

            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/sellerbuyer_login") // Input number
    public ResponseEntity<Map<String, String>> sellerBuyerLogin (@RequestBody OTP_DTO otpDetails) {

        String phoneNumber=otpDetails.getPhoneNumber();
        Boolean isPresent = sellerBuyerService.isPhoneNumberInCache(phoneNumber);

        Map<String, String> response = new HashMap<>();

        if (isPresent) {
            String otp = otpService.generateRandomOTP();
            otpStorageService.storeOTP(phoneNumber, otp);
            System.out.println(otpStorageService.getStoredOTPs());

            otpService.sendOTP(phoneNumber, otp);
//            sellerBuyerService.setRoleForUser(phoneNumber, "USER");

            response.put("message", "OTP generated");
            response.put("phoneNumber", phoneNumber);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "Registration required");
            response.put("phoneNumber", phoneNumber);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }



    @PostMapping("/otp-authenticate")
    public String authenticateWithOtp(@RequestBody OTP_DTO otpDetails) {
        if (otpStorageService.validateOtp(otpDetails.getPhoneNumber(), otpDetails.getEnteredOTP())) {
            UserDetails userDetails = userInfoService.loadUserByMobileNumber(otpDetails.getPhoneNumber());
            return jwtService.generateToken(userDetails.getUsername());
        } else {
            return "Invalid OTP";
        }
    }



    //Additional Open APIs

    @GetMapping("/checkPhoneNumber/{phoneNumber}")
    public ResponseEntity<String> checkPhoneNumber(@PathVariable String phoneNumber) {
        // Check the cache first
        if (sellerBuyerService.isPhoneNumberInCache(phoneNumber)) {
            return ResponseEntity.badRequest().body("Phone number already registered.");
        }
        // If not in cache, check the database(can be possible)
        else {
            return ResponseEntity.ok("Phone number is available.");
        }
    }


    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<String> checkEmail(@PathVariable String email) {
        // Check the cache first
        if (sellerBuyerService.isEmailInCache(email)) {
            return ResponseEntity.badRequest().body("Email  already registered.");
        }
        // If not in cache, check the database(can be possible)
        else {
            return ResponseEntity.ok("Email number is available.");
        }
    }

}
