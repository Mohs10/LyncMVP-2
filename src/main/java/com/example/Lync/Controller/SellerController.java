package com.example.Lync.Controller;

import com.example.Lync.Config.JwtService;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<SellerBuyerDTO> sellerDetails() {
        // Get the Authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the username from the Authentication object
        String username = authentication.getName();

        // Fetch SellerBuyer details based on email (username)
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        SellerBuyerDTO sellerBuyerDTO = sellerBuyerService.convertToSellerBuyerDTO(sellerDetails);

        // Return the seller details wrapped in ResponseEntity
        return ResponseEntity.ok(sellerBuyerDTO);
    }



    @PutMapping(value = "/editUser")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")

    public ResponseEntity<String> editUserDetails(@RequestBody SellerBuyerDTO sellerBuyerDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the username from the Authentication object
        String username = authentication.getName();

        // Fetch SellerBuyer details based on email (username)
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        sellerBuyerService.editSellerBuyer(sellerDetails.getUserId(), sellerBuyerDTO);
        System.out.println(sellerBuyerDTO);
        return ResponseEntity.ok("User Details Edited Successfully");
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
    public ResponseEntity<SellerProduct> addSellerProduct(@RequestBody SellerProductDTO sellerProductDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Fetch SellerBuyer details based on email (username)
            SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                    new RuntimeException("SellerBuyer details not found for email: " + username)
            );

            sellerProductDto.setSellerId(sellerDetails.getUserId());

            SellerProduct addedSellerProduct = sellerBuyerService.addSellerProduct(sellerProductDto);

            return ResponseEntity.ok(addedSellerProduct); // Returns the saved SellerProduct with 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/uploadProductImage1/{sellerProductId}")
    public ResponseEntity<String> uploadProductImage1(@PathVariable String sellerProductId,
                                                      @RequestParam("productImage1") MultipartFile productImage1) throws Exception {
        try {
            if (productImage1 != null && !productImage1.isEmpty()) {
                String imageUrl = sellerBuyerService.uploadSellerProductImage1(sellerProductId, productImage1);
                return ResponseEntity.ok("Product Image 1 uploaded successfully: " + imageUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Image 1 is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading Product Image 1.");
        }
    }
    @PostMapping("/uploadProductImage2/{sellerProductId}")
    public ResponseEntity<String> uploadProductImage2(@PathVariable String sellerProductId,
                                                      @RequestParam("productImage2") MultipartFile productImage2) throws Exception {
        try {
            if (productImage2 != null && !productImage2.isEmpty()) {
                String imageUrl = sellerBuyerService.uploadSellerProductImage2(sellerProductId, productImage2);
                return ResponseEntity.ok("Product Image 2 uploaded successfully: " + imageUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Image 2 is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading Product Image 2.");
        }
    }
    @PostMapping("/uploadCertificate/{sellerProductId}")
    public ResponseEntity<String> uploadCertificate(@PathVariable String sellerProductId,
                                                    @RequestParam("certificate") MultipartFile certificate) throws Exception {
        try {
            if (certificate != null && !certificate.isEmpty()) {
                String certificateUrl = sellerBuyerService.uploadSellerProductCertificate(sellerProductId, certificate);
                return ResponseEntity.ok("Certificate uploaded successfully: " + certificateUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Certificate is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading Certificate.");
        }
    }




    @PutMapping("/editSellerProduct/{spId}")
    public ResponseEntity<SellerProduct> editSellerProduct(@PathVariable("spId") String spId,
                                                           @RequestBody SellerProductDTO sellerProductDto) throws Exception {

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

//        if (productImage1 != null && !productImage1.isEmpty()) {
//            sellerProductDto.setProductImage1(productImage1);
//        }
//        if (productImage2 != null && !productImage2.isEmpty()) {
//            sellerProductDto.setProductImage2(productImage2);
//        }
//        if (certificate != null && !certificate.isEmpty()) {
//            sellerProductDto.setCertificationFile(certificate);
//        }

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

    @PostMapping("/sellerAddAddress")
    public ResponseEntity<String> addAddress(@RequestBody SellerBuyerAddressDTO sellerBuyerAddressDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = sellerBuyerService.addAddress(sellerDetails.getUserId(), sellerBuyerAddressDTO);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/myAddresses")
    public ResponseEntity<List<SellerBuyerAddressDTO>> getAddress(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        List<SellerBuyerAddressDTO> addressDTOS = sellerBuyerService.userGetsAddresses(sellerDetails.getUserId());
        return ResponseEntity.ok(addressDTOS);
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

    @GetMapping("/getInquiryById/{snId}")
    public ResponseEntity<SellerReceiveInquiryDTO> sellerOpenInquiry(@PathVariable Long snId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        return ResponseEntity.ok(inquiryService.sellerOpenInquiry(snId, sellerDetails.getUserId()));
    }

    @PostMapping("/sellerNegotiate/{snId}")
    public ResponseEntity<String> sellerNegotiatePrice(@PathVariable Long snId, @RequestBody Map<String, Double> requestBody){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        Double amount = requestBody.get("amount");
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


    @PostMapping("/sellerRejectQuery/{snId}")
    public ResponseEntity<String> sellerRejectQuery(@PathVariable Long snId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.sellerRejectQuery(snId, sellerDetails.getUserId() );
        return ResponseEntity.ok(message);
    }

    @PostMapping("/acceptAdminPrice/{snId}")
    public ResponseEntity<String> acceptAdminPrice(@PathVariable Long snId) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        String message= inquiryService.sellerAcceptAdminPrice(snId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }


    @PostMapping("/rejectAdminPrice/{snId}")
    public ResponseEntity<String> rejectAdminPrice(@PathVariable Long snId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.sellerRejectAdminPrice(snId, sellerDetails.getUserId() );
        return ResponseEntity.ok(message);
    }

    @GetMapping("/sellerGetsSampleOrders")
    public ResponseEntity<List<SampleOrderDTO>> sellerSampleOrders(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        System.out.println(username);
        return ResponseEntity.ok(inquiryService.sellerGetsAllSampleOrders(sellerDetails.getUserId()));
    }

    @GetMapping("/sellerSampleOrderById/{soId}")
    public ResponseEntity<SampleOrderDTO> sellerSampleOrderById(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        return ResponseEntity.ok(inquiryService.sellerGetsSampleOrderById(soId, sellerDetails.getUserId()));
    }

    @PostMapping("/sellerApproveSampleOrder/{soId}")
    public ResponseEntity<String> approveSampleOrder(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String s = inquiryService.sellerApproveSampleOrder(soId, sellerDetails.getUserId());
        return ResponseEntity.ok(s);
    }

    @PostMapping("/sellerDeclineSampleOrder/{soId}")
    public ResponseEntity<String> declineSampleOrder(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.sellerDeclineSampleOrder(soId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/sellerPackagingSample/{soId}")
    public ResponseEntity<String> packagingSample(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.sellerPackagingSample(soId,sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/sellerDispatchSample/{soId}")
    public ResponseEntity<String> sellerDispatchSample(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.sellerDispatchSampleToAdmin(soId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }













}
