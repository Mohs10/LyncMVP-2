package com.example.Lync.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OrderService {

    String buyerUploadPurchaseOrder(String qId, MultipartFile file, String buyerId) throws IOException;

    String adminUploadPurchaseInvoice(String oId, MultipartFile file) throws IOException;

    String adminUploadPurchaseOrder(String oId, MultipartFile file) throws IOException;

    String adminNotifyBuyerToPay(String oId, Double amount);

//    String buyer1stPayment(String oId, String buyerId);

    String adminNotifySellerToDispatch(String oId);

    String sellerProcessingOrder(String oId, String sellerId);

    String sellerDispatchedOrder(String oId, String sellerId);

    String sellerUploadOrderLoadedVehicleImg(String oId, String sellerId, MultipartFile file) throws IOException;

    String sellerUploadLoadedSealedVehicleImg(String oId, String sellerId, MultipartFile file) throws IOException;

    String sellerUploadEWayBill(String oId, String sellerId, MultipartFile file) throws IOException;

    String sellerUploadPaymentInvoice(String oId, String sellerId, MultipartFile file) throws IOException;

    String sellerUploadLRCopy(String oId, String sellerId, MultipartFile file) throws IOException;

    String sellerUploadWeightSlipPreLoad(String oId, String sellerId, MultipartFile file) throws IOException;

    String sellerUploadWeightSlipPostLoad(String oId, String sellerId, MultipartFile file) throws IOException;

    String adminReceivedOrder(String oId);

    String adminProcessingOrder(String oId);

    String adminDispatchedOrder(String oId);

    String adminUploadEWayBill(String oId, MultipartFile file) throws IOException;

    String adminUploadPaymentInvoice(String oId, MultipartFile file) throws IOException;

    String adminUploadLRCopy(String oId, MultipartFile file) throws IOException;

    String adminUploadWeightSlipPreLoad(String oId, MultipartFile file) throws IOException;

    String adminUploadWeightSlipPostLoad(String oId, MultipartFile file) throws IOException;

    String sellerUploadTransactionCertificate(String oId, String sellerId, MultipartFile file) throws IOException;

    String adminUploadTransactionCertificate(String oId, MultipartFile file) throws IOException;

    String buyerReceivedOrder(String oId, String buyerId);

    String paymentIdReceived (String orderId, String paymentId , String buyerId);


}
