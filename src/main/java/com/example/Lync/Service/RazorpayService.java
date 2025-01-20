package com.example.Lync.Service;

import com.example.Lync.DTO.RazorpayOrderDTO;
import com.example.Lync.DTO.RazorpayPaymentDTO;
import com.razorpay.Order;
import com.razorpay.Payment;

import java.util.List;

public interface RazorpayService {
    String createOrder(double amount, String currency, String receipt) throws Exception;

    boolean verifySignature(String orderId, String paymentId, String signature);

    public List<RazorpayOrderDTO> fetchAllOrders() throws Exception ;
    public List<RazorpayPaymentDTO> fetchAllPayments() throws Exception ;
    }
