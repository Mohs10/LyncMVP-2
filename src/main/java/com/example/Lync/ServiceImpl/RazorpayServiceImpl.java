package com.example.Lync.ServiceImpl;

import com.example.Lync.DTO.RazorpayCardDTO;
import com.example.Lync.DTO.RazorpayOrderDTO;
import com.example.Lync.DTO.RazorpayPaymentDTO;
import com.example.Lync.Service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.Refund;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Autowired
    public RazorpayServiceImpl(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    @Override
    public String createOrder(double amount, String currency, String receipt) throws Exception {
        try {
            int amountInPaise = (int) (amount * 100); // Convert amount to paise for Razorpay

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);

            // Creating order in Razorpay
            Order order = razorpayClient.orders.create(orderRequest);
            return order.toString();
        } catch (Exception e) {
            throw new Exception("Failed to create order: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            // Verifying signature using Razorpay's utility function
            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String createPartialPaymentOrder(double amount, String currency, String receipt) throws Exception {
        try {
            int amountInPaise = (int) (amount * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            orderRequest.put("partial_payment", true); // Enable partial payments

            Order order = razorpayClient.orders.create(orderRequest);
            return order.toString();
        } catch (Exception e) {
            throw new Exception("Failed to create partial payment order: " + e.getMessage(), e);
        }
    }

    public String createInstallmentPlan(String paymentId, double[] installments) throws Exception {
        try {
            JSONObject installmentPlanRequest = new JSONObject();
            installmentPlanRequest.put("payment_id", paymentId);
            installmentPlanRequest.put("installments", installments);

            // Create installment plan (requires Razorpay support for this API)
            return "Installment plan created for payment ID: " + paymentId;
        } catch (Exception e) {
            throw new Exception("Failed to create installment plan: " + e.getMessage(), e);
        }
    }

    public String processRefund(String paymentId, double refundAmount) throws Exception {
        try {
            Map<String, Object> refundRequest = new HashMap<>();
            refundRequest.put("payment_id", paymentId);
            refundRequest.put("amount", (int) (refundAmount * 100)); // Convert to paise

            Refund refund = razorpayClient.refunds.create(new JSONObject(refundRequest));
            return refund.toString();
        } catch (Exception e) {
            throw new Exception("Failed to process refund: " + e.getMessage(), e);
        }
    }



    public List<RazorpayOrderDTO> fetchAllOrders() throws Exception {
        try {
            // Fetch all orders for Admin from Razorpay
            JSONObject options = new JSONObject();
            options.put("count", 100); // Limit the number of orders fetched

            List<Order> orders = razorpayClient.orders.fetchAll(options); // Pass options to fetchAll

            // Convert to DTO list
            return orders.stream()
                    .map(this::toOrderDto) // Convert each order to a DTO
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Failed to fetch all orders: " + e.getMessage(), e);
        }
    }


    public List<RazorpayPaymentDTO> fetchAllPayments() throws Exception {
        try {
            // Fetch all orders for Admin from Razorpay
            JSONObject options = new JSONObject();
            options.put("count", 100); // Limit the number of orders fetched

            List<Payment> payments = razorpayClient.payments.fetchAll(options); // Pass options to fetchAll

            // Convert to DTO list
            return payments.stream()
                    .map(this::toPaymentDto) // Convert each order to a DTO
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Failed to fetch all payments: " + e.getMessage(), e);
        }
    }


    private RazorpayOrderDTO toOrderDto(Order order) {
        RazorpayOrderDTO dto = new RazorpayOrderDTO();

        dto.setId(getStringOrDefault(order, "id"));
        dto.setReceipt(getStringOrDefault(order, "receipt"));
        dto.setStatus(getStringOrDefault(order, "status"));
        dto.setAmount(order.get("amount"));
        dto.setCurrency(getStringOrDefault(order, "currency"));

        // Safely handle amount_paid, amount_due, and attempts (they can be null)
        dto.setAmountPaid(getIntegerOrDefault(order, "amount_paid"));
        dto.setAmountDue(getIntegerOrDefault(order, "amount_due"));
        dto.setAttempts(getIntegerOrDefault(order, "attempts"));

        // Handle created_at: Ensure it's a Date and convert to long (Unix timestamp)
        Object createdAtObj = order.get("created_at");
        if (createdAtObj instanceof Date) {
            dto.setCreatedAt(((Date) createdAtObj).getTime()); // Convert Date to long
        } else {
            dto.setCreatedAt(0); // Default value if not a Date
        }

        return dto;
    }

    // Helper method to safely get a String from the order, or return a default value if null
    private String getStringOrDefault(Order order, String field) {
        Object value = order.get(field);
        if (value instanceof String) {
            return (String) value;
        }
        return ""; // Return empty string if null or unexpected type
    }

    // Helper method to safely get an Integer from the order, or return 0 if null
    private int getIntegerOrDefault(Order order, String field) {
        Object value = order.get(field);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            return ((Long) value).intValue(); // If it's a Long, convert to int
        }
        return 0; // Return default value if null or unexpected type
    }

    private RazorpayPaymentDTO toPaymentDto(Payment payment) {
        RazorpayPaymentDTO paymentDTO = new RazorpayPaymentDTO();

        paymentDTO.setId(getStringOrDefault(payment, "id"));
        paymentDTO.setOrderId(getStringOrDefault(payment, "order_id"));
        paymentDTO.setContact(getStringOrDefault(payment, "contact"));
        paymentDTO.setEmail(getStringOrDefault(payment, "email"));
        paymentDTO.setCurrency(getStringOrDefault(payment, "currency"));
        paymentDTO.setAmount(getIntegerOrDefault(payment, "amount"));
        paymentDTO.setAmountRefunded(getIntegerOrDefault(payment, "amount_refunded"));
        paymentDTO.setMethod(getStringOrDefault(payment, "method"));
        paymentDTO.setStatus(getStringOrDefault(payment, "status"));

        // Handle 'created_at' field with proper null checks
        Object createdAtObj = payment.get("created_at");
        if (createdAtObj instanceof Date) {
            paymentDTO.setCreatedAt(((Date) createdAtObj).getTime()); // Convert Date to long (Unix timestamp)
        } else if (createdAtObj instanceof Long) {
            paymentDTO.setCreatedAt((Long) createdAtObj); // Directly cast if it's already a Long
        } else {
            paymentDTO.setCreatedAt(0); // Default value if it's neither Date nor Long
        }

        paymentDTO.setFee(getIntegerOrDefault(payment, "fee"));
        paymentDTO.setDescription(getStringOrDefault(payment, "description"));
        paymentDTO.setErrorReason(getStringOrDefault(payment, "error_reason"));
        paymentDTO.setErrorDescription(getStringOrDefault(payment, "error_description"));
        paymentDTO.setErrorSource(getStringOrDefault(payment, "error_source"));
        paymentDTO.setErrorStep(getStringOrDefault(payment, "error_step"));
        paymentDTO.setErrorCode(getStringOrDefault(payment, "error_code"));
        paymentDTO.setInternational(getBooleanOrDefault(payment, "international"));
        paymentDTO.setWallet(getStringOrDefault(payment, "wallet"));
        paymentDTO.setVpa(getStringOrDefault(payment, "vpa"));
        paymentDTO.setInvoiceId(getStringOrDefault(payment, "invoice_id"));
        paymentDTO.setNotes(getStringOrDefault(payment, "notes"));
        paymentDTO.setAcquirerAuthCode(getStringOrDefault(payment, "acquirer_data.auth_code"));
        paymentDTO.setTax(getStringOrDefault(payment, "tax"));

        // Check if card data is available and extract it
        if (payment.get("card") != null) {
            RazorpayCardDTO cardDTO = new RazorpayCardDTO();
            JSONObject cardData = (JSONObject) payment.get("card"); // This is a JSONObject, not a Map

            cardDTO.setId(getStringOrDefault(cardData, "id"));
            cardDTO.setNetwork(getStringOrDefault(cardData, "network"));
            cardDTO.setType(getStringOrDefault(cardData, "type"));
            cardDTO.setSubType(getStringOrDefault(cardData, "sub_type"));
            cardDTO.setEmi(getBooleanOrDefault(cardData, "emi"));
            cardDTO.setIssuer(getStringOrDefault(cardData, "issuer"));
            cardDTO.setLast4(getStringOrDefault(cardData, "last4"));
            cardDTO.setInternational(getBooleanOrDefault(cardData, "international"));

            paymentDTO.setCardDTO(cardDTO);
        }

        return paymentDTO;
    }

    // Helper method to safely get a String from the payment data
    private String getStringOrDefault(Payment payment, String key) {
        Object value = payment.get(key);
        if (value instanceof String) {
            return (String) value;
        } else if (value == null) {
            return ""; // Return empty string if value is null
        }
        return ""; // Default value if value is unexpected type
    }

    // Helper method to safely get an Integer from the payment data
    private int getIntegerOrDefault(Payment payment, String key) {
        Object value = payment.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            return ((Long) value).intValue(); // If it's a Long, convert to int
        } else if (value == null) {
            return 0; // Default value if value is null
        }
        return 0; // Default value if value is unexpected type
    }

    // Helper method to safely get a Boolean from the payment data
    private boolean getBooleanOrDefault(Payment payment, String key) {
        Object value = payment.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value == null) {
            return false; // Default value if value is null
        }
        return false; // Default value if value is unexpected type
    }

    // Helper method to safely get a String from a JSONObject (used for card data)
    private String getStringOrDefault(JSONObject json, String key) {
        Object value = json.opt(key);
        if (value instanceof String) {
            return (String) value;
        } else if (value == null) {
            return ""; // Return empty string if value is null
        }
        return ""; // Default value if value is unexpected type
    }

    // Helper method to safely get a Boolean from a JSONObject (used for card data)
    private boolean getBooleanOrDefault(JSONObject json, String key) {
        Object value = json.opt(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value == null) {
            return false; // Default value if value is null
        }
        return false; // Default value if value is unexpected type
    }





}
