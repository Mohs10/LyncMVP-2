package com.example.Lync.Controller;

import com.example.Lync.Config.JwtService;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.DTO.UserInfoDTO;
import com.example.Lync.Entity.AdminAddress;
import com.example.Lync.Repository.SellerBuyerRepository;
import com.example.Lync.Repository.UserInfoRepository;
import com.example.Lync.Service.*;
import com.example.Lync.ServiceImpl.UserInfoService;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataInput;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;


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
    private final AdminAddressService adminAddressService;

    // Removed the duplicate UserInfoService field
    public AdminController(S3Service s3Service,
                           UserInfoRepository repository,
                           SellerBuyerRepository sellerBuyerRepository,
                           UserInfoService service, // Keep only one UserInfoService
                           JwtService jwtService,
                           SellerBuyerService sellerBuyerService,
                           AuthenticationManager authenticationManager,
                           OtpService otpService,
                           OTPStorageService otpStorageService, InquiryService inquiryService, AdminAddressService adminAddressService) {
        this.s3Service = s3Service;
        this.service = service;
        this.jwtService = jwtService;
        this.sellerBuyerService = sellerBuyerService;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
        this.otpStorageService = otpStorageService;
        this.inquiryService = inquiryService;
        this.adminAddressService = adminAddressService;
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

//    @PutMapping(value = "/editUser/{userId}")
//    public ResponseEntity<String> editUser(@PathVariable String userId,
//                                           @RequestPart("sellerBuyerDTO") SellerBuyerDTO sellerBuyerDTO,
//                                           @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
//                                           @RequestPart(value = "certificate", required = false) MultipartFile certificate,
//                                           @RequestPart(value = "cancelledCheque", required = false) MultipartFile cancelledCheque,
//                                           HttpServletRequest request) throws IOException {
//
//        // Log the content type and headers
//        System.out.println("Content-Type: " + request.getContentType());
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            System.out.println(headerName + ": " + request.getHeader(headerName));
//        }
//
//        // Continue with user editing logic
//
//
//        sellerBuyerService.editSellerBuyer(userId, sellerBuyerDTO);
//        return ResponseEntity.ok("User Edited Successfully");
//    }



    // 1. Edit Basic User Details
    @PutMapping(value = "/editUser/{userId}")
    public ResponseEntity<String> editUserDetails(@PathVariable String userId,
                                                  @RequestBody SellerBuyerDTO sellerBuyerDTO) {
        sellerBuyerService.editSellerBuyer(userId, sellerBuyerDTO);


        System.out.println(sellerBuyerDTO);
        return ResponseEntity.ok("User Details Edited Successfully");
    }

    // 2. Update Profile Picture
    @PutMapping(value = "/updateProfilePicture/{userId}")
    public ResponseEntity<String> updateProfilePicture(@PathVariable String userId,
                                                       @RequestParam("profilePicture") MultipartFile profilePicture) throws IOException {

        System.out.println("image");
        sellerBuyerService.uploadProfilePicture(userId, profilePicture);
        return ResponseEntity.ok("Profile Picture Updated Successfully");
    }

    // 3. Upload Certificate
    @PutMapping(value = "/uploadCertificate/{userId}")
    public ResponseEntity<String> uploadCertificate(@PathVariable String userId,
                                                    @RequestParam("certificate") MultipartFile certificate) throws IOException {

        System.out.println("uploadCertificate");
        sellerBuyerService.uploadCertificate(userId, certificate);
        return ResponseEntity.ok("Certificate Uploaded Successfully");
    }

    // 4. Upload Cancelled Cheque
    @PutMapping(value = "/uploadCancelledCheque/{userId}")
    public ResponseEntity<String> uploadCancelledCheque(@PathVariable String userId,
                                                        @RequestParam("cancelledCheque") MultipartFile cancelledCheque) throws IOException {

        System.out.println("uploadCancelledCheque");
        sellerBuyerService.uploadCancelledCheque(userId, cancelledCheque);
        return ResponseEntity.ok("Cancelled Cheque Uploaded Successfully");
    }

    //Admin Address
    @PostMapping("/addAddress")
    public ResponseEntity<AdminAddress> addAdminAddress(@RequestBody AdminAddress adminAddress) {
        AdminAddress savedAddress = adminAddressService.addAdminAddress(adminAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
    }

    // GET endpoint to retrieve all AdminAddresses
    @GetMapping("/getAllAddress")
    public ResponseEntity<List<AdminAddress>> getAllAdminAddresses() {
        List<AdminAddress> addresses = adminAddressService.getAllAdminAddresses();
        return ResponseEntity.ok(addresses);
    }






    @GetMapping("/allInquiries")
    public ResponseEntity<?> getInquiries(){
        return ResponseEntity.ok(inquiryService.adminGetAllInquiry());
    }

    @GetMapping("/getInquiryById/{qId}")
    public ResponseEntity<?> getInquiryById(@PathVariable String qId) throws Exception {
        return ResponseEntity.ok(inquiryService.adminGetInquiryByQId(qId));
    }

    @PostMapping("/sendInquiryToSeller/{qId}")
    public ResponseEntity<String> sendInquiry(@PathVariable String qId, @RequestBody InquiryDTO inquiryDTO){
        String responseMessage = inquiryService.sendInquiryToSeller(qId, inquiryDTO);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/sellerSellingProduct/{productId}/{productFormId}/{productVarietyId}")
    public ResponseEntity<List<SellerProductDTO>> sellersSellingProducts(@PathVariable Long productId,
                                                                         @PathVariable Long productFormId,
                                                                         @PathVariable Long productVarietyId){
        return ResponseEntity.ok(inquiryService.sellersSellingProduct(productId, productFormId, productVarietyId));
    }

    @PostMapping("/sendFinalPrice/{snId}")
    public ResponseEntity<String> sendFinalPrice(@PathVariable Long snId, @RequestBody Map<String, Double> requestBody){

        Double amount =  requestBody.get("amount");

        String message = inquiryService.adminFinalPriceToSeller(snId, amount);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/adminSelectsSeller/{snId}")
    public ResponseEntity<String> adminSelectedSeller(@PathVariable Long snId){
        String message = inquiryService.adminSelectsSeller(snId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/adminQuoteToBuyer/{qId}")
    public ResponseEntity<String> quoteToBuyer(@PathVariable String qId, @RequestBody InquiryDTO inquiryDTO){
        String message = inquiryService.adminQuoteToBuyer(qId, inquiryDTO);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/adminSentFinalPriceToBuyer/{qId}")
    public ResponseEntity<String> finalPriceToBuyer(@PathVariable String qId, @RequestBody Double amount) {
        String message = inquiryService.adminFinalPriceToBuyer(qId, amount);
        return ResponseEntity.ok(message);
    }

}