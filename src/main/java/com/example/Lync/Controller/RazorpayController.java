package com.example.Lync.Controller;

import com.example.Lync.DTO.RazorpayOrderDTO;
import com.example.Lync.DTO.RazorpayPaymentDTO;
import com.example.Lync.Service.RazorpayService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class RazorpayController {

    @Autowired
    private RazorpayService razorpayService;

    // API to create an order
    @PostMapping("/create-order")
//    @PreAuthorize("hasAuthority('ROLE_BUYER')")

    public ResponseEntity<String> createOrder(
            @RequestParam double amount,
            @RequestParam String currency,
            @RequestParam String receipt) {
        try {
            String orderResponse = razorpayService.createOrder(amount, currency, receipt);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API to verify payment signature
// API to verify payment signature
    @PostMapping("/verify-signature")
//    @PreAuthorize("hasAuthority('ROLE_BUYER')")

    public ResponseEntity<String> verifySignature(
            @RequestParam String orderId,
            @RequestParam String paymentId,
            @RequestParam String signature) {
        boolean isVerified = razorpayService.verifySignature(orderId, paymentId, signature);
        if (isVerified) {
            return ResponseEntity.ok("{\"status\": \"Payment verification successful!\"}");
        } else {
            return ResponseEntity.status(400).body("{\"status\": \"Payment verification failed.\"}");
        }
    }


    @PostMapping("/processRefund")
    public String processRefund(@RequestParam String paymentId, @RequestParam double refundAmount) {
        try {
            System.out.println("Here we go ");
            return razorpayService.processRefund(paymentId, refundAmount);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/allOrders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<?> fetchAllOrders() {
        try {
            List<RazorpayOrderDTO> allOrders = razorpayService.fetchAllOrders();
            return ResponseEntity.ok(allOrders);
        } catch (Exception e) {
            System.err.println("Error fetching orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch orders. Please try again later.");
        }
    }

    @GetMapping("/allPayments")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<?> fetchAllPayments() {
        try {
            List<RazorpayPaymentDTO> allPayment =  razorpayService.fetchAllPayments();
            return ResponseEntity.ok(allPayment);
        } catch (Exception e) {
            System.err.println("Error fetching orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch orders. Please try again later.");
        }
    }
}
