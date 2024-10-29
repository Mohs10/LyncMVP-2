package com.example.Lync.Controller;

import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SampleOrderDTO;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Repository.SellerBuyerRepository;
import com.example.Lync.Service.InquiryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/inquiry")
public class InquiryController {

    private InquiryService inquiryService;
    private SellerBuyerRepository sellerBuyerRepository;

//    @PostMapping("/addInquiry")
//    public ResponseEntity<String> addInquiry(@RequestBody InquiryDTO inquiryDTO){
//        inquiryService.addInquiry(inquiryDTO, );
//        return ResponseEntity.ok("Inquiry added successfully");
//    }

//    @GetMapping("/allInquiries")
//    public ResponseEntity<?> getInquiries(){
//        return ResponseEntity.ok(inquiryService.getAllInquiries());
//    }
//
//    @GetMapping("/getInquiryById/{qId}")
//    public ResponseEntity<?> getInquiryById(@PathVariable String qId) throws Exception {
//        return ResponseEntity.ok(inquiryService.getInquiryByQId(qId));
//    }

//    @PostMapping("/sendInquiryToSeller/{qId}")
//    public ResponseEntity<String> sendInquiry(@PathVariable String qId, @RequestBody InquiryDTO inquiryDTO){
//        inquiryService.sendInquiryToSeller(qId, inquiryDTO);
//        return ResponseEntity.ok("Sent Inquiry to Seller");
//    }

//    @GetMapping("/sellerAllInquiries")
//    public ResponseEntity<List<InquiryDTO>> sellerAllInquiries(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
//                new RuntimeException("SellerBuyer details not found for email: " + username)
//        );
//        return ResponseEntity.ok(inquiryService.sellerAllInquiries(sellerDetails.getUserId()));
//    }

//    @GetMapping("/sellerNewInquiries")
//    public ResponseEntity<List<InquiryDTO>> sellerNewInquiries(){
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        SellerBuyer sellerDetails = sellerBuyerRepository.findByEmail(username).orElseThrow(() ->
//                new RuntimeException("SellerBuyer details not found for email: " + username)
//        );
//        return ResponseEntity.ok(inquiryService.sellerNewInquiries(sellerDetails.getUserId()));
//    }

//    @GetMapping("/sellerOpenInquiry/{qId}")
//    public ResponseEntity<InquiryDTO> sellerOpenInquiry(@PathVariable String qId) throws Exception {
//        return ResponseEntity.ok(inquiryService.sellerOpenInquiry(qId));
//    }

//    @PostMapping("/acceptInquiry/{qId}")
//    public ResponseEntity<String> sellerAcceptQuery(@PathVariable String qId, @RequestBody String description) throws Exception {
//        inquiryService.sellerAcceptQuery(qId, description);
//        return ResponseEntity.ok("Accepted Inquiry with ID" + qId);
//    }

//    @PostMapping("/sellerRejectQuery/{qId}")
//    public ResponseEntity<String> sellerRejectQuery(@PathVariable String qId, @RequestBody String description) throws Exception {
//        inquiryService.sellerRejectQuery(qId, description);
//        return ResponseEntity.ok("Rejected Inquiry for ID" + qId);
//    }

    @PostMapping("/sellerOrderSample/{qId}")
    public ResponseEntity<String> sellerOrderSample(@PathVariable String qId, @RequestBody SampleOrderDTO sampleOrderDTO) throws Exception {
        inquiryService.sellerOrderSample(qId, sampleOrderDTO);
        return ResponseEntity.ok("Sample Ordered for ID" + qId);
    }
}
