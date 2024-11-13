package com.example.Lync.Controller;

import com.example.Lync.Config.JwtService;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.DTO.SellerReceiveInquiryDTO;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Entity.SellerProduct;
import com.example.Lync.Repository.SellerBuyerRepository;
import com.example.Lync.Repository.SellerProductRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final SellerProductRepository sellerProductRepository;
    private final InquiryService inquiryService;

    public SellerController(S3Service s3Service, UserInfoRepository repository, SellerBuyerRepository sellerBuyerRepository, UserInfoService service, JwtService jwtService, SellerBuyerService sellerBuyerService, AuthenticationManager authenticationManager, OtpService otpService, OTPStorageService otpStorageService, UserInfoService userInfoService, SellerProductRepository sellerProductRepository, InquiryService inquiryService) {
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
        this.sellerProductRepository = sellerProductRepository;
        this.inquiryService = inquiryService;
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
    public ResponseEntity<SellerProduct> addSellerProduct(@RequestPart("sellerProductDto") SellerProductDTO sellerProductDto,
     @RequestParam(value = "productImage1", required = false) MultipartFile productImage1,
      @RequestParam(value = "productImage2", required = false) MultipartFile productImage2,
       @RequestParam(value = "certificate", required = false) MultipartFile certificate

    ) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the username from the Authentication object
        String username = authentication.getName();

        // Fetch SellerBuyer details based on email (username)
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        sellerProductDto.setSellerId(sellerDetails.getUserId());

        if (productImage1 != null && !productImage1.isEmpty()) {
            sellerProductDto.setProductImage1(productImage1);
        }
        if (productImage2 != null && !productImage2.isEmpty()) {
            sellerProductDto.setProductImage2(productImage2);
        }
        if (certificate != null && !certificate.isEmpty()) {
            sellerProductDto.setCertificationFile(certificate);
        }

        SellerProduct addedSellerProduct = sellerBuyerService.addSellerProduct(sellerProductDto);

        return ResponseEntity.ok(addedSellerProduct); // Returns the saved SellerProduct with 200 OK
    }


    @PutMapping("/editSellerProduct/{spId}")
    public ResponseEntity<SellerProduct> editSellerProduct(@PathVariable("spId") String spId,
                                                           @RequestPart("sellerProductDto") SellerProductDTO sellerProductDto,
                                                           @RequestParam(value = "productImage1", required = false) MultipartFile productImage1,
                                                           @RequestParam(value = "productImage2", required = false) MultipartFile productImage2,
                                                           @RequestParam(value = "certificate", required = false) MultipartFile certificate) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch SellerBuyer details based on email (username)
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        sellerProductDto.setSellerId(sellerDetails.getUserId());

        // Fetch the existing SellerProduct
        SellerProduct existingSellerProduct = sellerProductRepository.findById(spId)
                .orElseThrow(() -> new RuntimeException("SellerProduct not found for SP ID: " + spId));

        if (productImage1 != null && !productImage1.isEmpty()) {
            sellerProductDto.setProductImage1(productImage1);
        }
        if (productImage2 != null && !productImage2.isEmpty()) {
            sellerProductDto.setProductImage2(productImage2);
        }
        if (certificate != null && !certificate.isEmpty()) {
            sellerProductDto.setCertificationFile(certificate);
        }

        SellerProduct updatedSellerProduct = sellerBuyerService.editSellerProduct(existingSellerProduct, sellerProductDto);

        return ResponseEntity.ok(updatedSellerProduct); // Returns the updated SellerProduct with 200 OK
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






    @GetMapping("/sellerAllInquiries")
    public ResponseEntity<List<SellerReceiveInquiryDTO>> sellerAllInquiries(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        return ResponseEntity.ok(inquiryService.sellerAllInquiries(sellerDetails.getUserId()));
    }

    @GetMapping("/newInquiries")
    public ResponseEntity<List<InquiryDTO>> sellerNewInquiries(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        return ResponseEntity.ok(inquiryService.sellerNewInquiries(sellerDetails.getUserId()));
    }

    @GetMapping("/sellerOpenInquiry/{snId}")
    public ResponseEntity<SellerReceiveInquiryDTO> sellerOpenInquiry(@PathVariable Long snId) throws Exception {
        return ResponseEntity.ok(inquiryService.sellerOpenInquiry(snId));
    }

    @PostMapping("/sellerNegotiate/{snId}")
    public ResponseEntity<String> sellerNegotiatePrice(@PathVariable Long snId, @RequestBody Double amount){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.sellerNegotiatePrice(snId, sellerDetails.getUserId(), amount);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/acceptInquiry/{snId}")
    public ResponseEntity<String> sellerAcceptQuery(@PathVariable Long snId) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        String message= inquiryService.sellerAcceptInquiry(snId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }


    @PostMapping("/sellerRejectQuery/{qId}")
    public ResponseEntity<String> sellerRejectQuery(@PathVariable String qId, @RequestBody String description) throws Exception {
        inquiryService.sellerRejectQuery(qId, description);
        return ResponseEntity.ok("Rejected Inquiry for ID" + qId);
    }

}
