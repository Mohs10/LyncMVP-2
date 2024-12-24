package com.example.Lync.Controller;

import com.example.Lync.Config.JwtService;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.*;
import com.example.Lync.Entity.FavouriteCategory;
import com.example.Lync.Entity.FavouriteProduct;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Repository.SellerBuyerRepository;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = {"http://localhost:5173", "http://lync-reactjs-bucket.s3-website.ap-south-1.amazonaws.com", "https://another-domain.com", "http://buyerwebportal.s3-website.ap-south-1.amazonaws.com"})
@RestController
@RequestMapping("/auth/buyer")
@PreAuthorize("hasAuthority('ROLE_BUYER')")

public class BuyerController {

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
    private final InquiryService inquiryService;

    public BuyerController(S3Service s3Service, UserInfoRepository repository, SellerBuyerRepository sellerBuyerRepository, UserInfoService service, JwtService jwtService, SellerBuyerService sellerBuyerService, AuthenticationManager authenticationManager, OtpService otpService, OTPStorageService otpStorageService, UserInfoService userInfoService, InquiryService inquiryService) {
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
        this.inquiryService = inquiryService;
    }

    @GetMapping("/buyerProfile")
    public String sellerProfile() {
        return "Welcome to Buyer Profile";
    }

    @GetMapping("/details")
    public ResponseEntity<SellerBuyerDTO> buyerDetails() {
        // Get the Authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the username from the Authentication object
        String username = authentication.getName();

        // Fetch user info based on email (username)
//        UserInfo userInfo = userInfoRepository.findByEmail(username).orElseThrow(() ->
//                new UsernameNotFoundException("User not found with email: " + username)
//        );

        // Fetch SellerBuyer details based on email (username)
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("Buyer details not found for email: " + username)
        );

        SellerBuyerDTO sellerBuyerDTO = sellerBuyerService.convertToSellerBuyerDTO(sellerDetails);

        // Return the seller details wrapped in ResponseEntity
        return ResponseEntity.ok(sellerBuyerDTO);
    }

    @PutMapping(value = "/editUser")
    @PreAuthorize("hasAuthority('ROLE_BUYER')")

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



    @PostMapping("/addFavouriteProduct")
    public ResponseEntity<FavouriteProduct> addFavouriteProduct(@RequestBody FavoriteProductAndCategory_DTO  dto) {
        FavouriteProduct favouriteProduct = sellerBuyerService.addFavouriteProduct( dto.getBuyerId(), dto.getProductId());
        return ResponseEntity.ok(favouriteProduct);
    }

    @GetMapping("/user/FavouriteProduct/{userId}")
    public ResponseEntity<List<FavouriteProduct>> getFavouriteProductsByUser(@PathVariable String userId) {
        List<FavouriteProduct> favouriteProducts = sellerBuyerService.getFavouriteProductsByUser(userId);
        return ResponseEntity.ok(favouriteProducts);
    }

    @GetMapping("/product/FavouriteProduct/{productId}")
    public ResponseEntity<List<FavouriteProduct>> getFavouriteProductsByProduct(@PathVariable Long productId) {
        List<FavouriteProduct> favouriteProducts = sellerBuyerService.getFavouriteProductsByProduct(productId);
        return ResponseEntity.ok(favouriteProducts);
    }

//    @DeleteMapping("/delete/{favProductId}")
//    public ResponseEntity<String> removeFavouriteProduct(@PathVariable Long favProductId) {
//        sellerBuyerService.removeFavouriteProduct(favProductId);
//        return ResponseEntity.ok("Favourite product removed successfully");
//    }

    @GetMapping("/FavouriteProduct/{favProductId}")
    public ResponseEntity<FavouriteProduct> getFavouriteProductById(@PathVariable Long favProductId) {
        Optional<FavouriteProduct> favouriteProduct = sellerBuyerService.getFavouriteProductById(favProductId);
        return favouriteProduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/addFavouriteCategory")
    public ResponseEntity<FavouriteCategory> addFavouriteCategory(@RequestBody FavoriteProductAndCategory_DTO  dto) {
        FavouriteCategory favouriteCategory = sellerBuyerService.addFavouriteCategory(dto.getBuyerId(), dto.getCategoryId());
        return ResponseEntity.ok(favouriteCategory);
    }

    @GetMapping("/user/FavouriteCategory/{userId}")
    public ResponseEntity<List<FavouriteCategory>> getFavouriteCategoriesByUser(@PathVariable String userId) {
        List<FavouriteCategory> favouriteCategories = sellerBuyerService.getFavouriteCategoriesByUser(userId);
        return ResponseEntity.ok(favouriteCategories);
    }

    @GetMapping("/category/FavouriteCategory/{categoryId}")
    public ResponseEntity<List<FavouriteCategory>> getFavouriteCategoriesByCategory(@PathVariable Long categoryId) {
        List<FavouriteCategory> favouriteCategories = sellerBuyerService.getFavouriteCategoriesByCategory(categoryId);
        return ResponseEntity.ok(favouriteCategories);
    }

//    @DeleteMapping("/delete/{favCategoryId}")
//    public ResponseEntity<String> removeFavouriteCategory(@PathVariable Long favCategoryId) {
//        sellerBuyerService.removeFavouriteCategory(favCategoryId);
//        return ResponseEntity.ok("Favourite category removed successfully");
//    }

    @GetMapping("/FavouriteCategory/{favCategoryId}")
    public ResponseEntity<FavouriteCategory> getFavouriteCategoryById(@PathVariable Long favCategoryId) {
        Optional<FavouriteCategory> favouriteCategory = sellerBuyerService.getFavouriteCategoryById(favCategoryId);
        return favouriteCategory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // API to allow a seller to become a seller
    @PostMapping("/becomeSeller/{userId}")
    public ResponseEntity<String> becomeSeller(@PathVariable String userId) {
        try {
            sellerBuyerService.becomeSeller(userId);
            return ResponseEntity.ok("User role updated to include ROLE_SELLER");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // Handle errors
        }
    }

    @PostMapping("/buyerAddAddress")
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

    @GetMapping("/userGetAddressById/{uaId}")
    public ResponseEntity<SellerBuyerAddressDTO> getAddressById(@PathVariable Long uaId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username =  authentication.getName();
        SellerBuyer buyerDetails = sellerBuyerRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User details not found for Email : " + username));
        SellerBuyerAddressDTO dto = sellerBuyerService.userGetAddressById(buyerDetails.getUserId(), uaId);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @PostMapping("/addInquiry")
    public ResponseEntity<String> addInquiry(@RequestBody InquiryDTO inquiryDTO) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message =  inquiryService.buyerAddInquiry(inquiryDTO, sellerDetails.getUserId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/buyerGetsAllInquiry")
    public ResponseEntity<List<InquiryDTO>> getInquiries(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        System.out.println("Controller sellerDetails : " + sellerDetails);
//        List<InquiryDTO> DTOs = inquiryService.buyerGetsAllInquiry(sellerDetails.getUserId());

        return new ResponseEntity<>(inquiryService.buyerGetsInquiries(sellerDetails.getUserId()), HttpStatus.OK);
    }

    @GetMapping("/buyerGetsInquiryById/{qId}")
    public ResponseEntity<InquiryDTO> getInquiryById(@PathVariable String qId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        return ResponseEntity.ok(inquiryService.buyerGetsInquiryById(sellerDetails.getUserId(), qId));
    }

//    @PostMapping("/buyerRejectsInquiry/{qId}")
//    public ResponseEntity<String> buyerReject(@PathVariable String qId, @RequestBody String description) throws Exception {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
//                new RuntimeException("SellerBuyer details not found for email: " + username)
//        );
//        inquiryService.buyerRejectsInquiries(qId, description, sellerDetails.getUserId());
//        return ResponseEntity.ok("Inquiry Declined Successfully.");
//    }

    @PostMapping("/buyerNegotiatePrice/{qId}")
    public ResponseEntity<String> negotiatePrice(@PathVariable String qId, @RequestBody Map<String, Double> requestBody) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        Double amount = requestBody.get("amount");
        String message = inquiryService.buyerNegotiatePrice(qId, sellerDetails.getUserId(), amount);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/buyerAcceptsQuery/{qId}")
    public ResponseEntity<String> acceptsQuery(@PathVariable String qId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.buyerAcceptQuery(qId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/buyerRejectQuery/{qId}")
    public ResponseEntity<String> rejectQuery(@PathVariable String qId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.buyerRejectedQuery(qId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/priceRange/{productId}")
    public ResponseEntity<PriceRangeProjection> priceRange(@PathVariable Long productId) {

        PriceRangeProjection priceRange = sellerBuyerService.priceRangeByProductId(productId);
        System.out.println(priceRange);
        return ResponseEntity.ok(priceRange);
    }

    @PostMapping("/buyerRequestSample/{qId}")
    public ResponseEntity<String> buyerRequestSample(@PathVariable String qId, @RequestBody SampleOrderDTO sampleOrderDTO) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.buyerRequestSample(qId, sellerDetails.getUserId(), sampleOrderDTO);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/buyerGetsSampleOrders")
    public ResponseEntity<List<SampleOrderDTO>> buyerGetsSampleOrders(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );

        return ResponseEntity.ok(inquiryService.buyerGetsAllSampleOrders(sellerDetails.getUserId()));
    }

    @GetMapping("/buyerSampleOrderById/{soId}")
    public ResponseEntity<SampleOrderDTO> buyerSampleOrderById(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        return ResponseEntity.ok(inquiryService.buyerGetsSampleOrderById(soId, sellerDetails.getUserId()));
    }

    @PostMapping("/buyerReceivedSample/{soId}")
    public ResponseEntity<String> receivedSample(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.buyerReceivedSample(soId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/buyerApprovedSample/{soId}")
    public ResponseEntity<String> approvedSample(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.buyerApprovedSample(soId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/buyerRejectedSample/{soId}")
    public ResponseEntity<String> rejectedSample(@PathVariable String soId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
                new RuntimeException("SellerBuyer details not found for email: " + username)
        );
        String message = inquiryService.buyerRejectedSample(soId, sellerDetails.getUserId());
        return ResponseEntity.ok(message);
    }




}
