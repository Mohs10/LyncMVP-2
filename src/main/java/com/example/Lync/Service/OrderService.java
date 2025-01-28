package com.example.Lync.Service;

import com.example.Lync.DTO.OrderDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    String buyerUploadPurchaseOrder(String qId, MultipartFile file, String buyerId) throws IOException;

    String adminUploadPurchaseInvoice(String oId, MultipartFile file) throws IOException;

    String adminUploadPurchaseOrder(String oId, MultipartFile file) throws IOException;

    String adminNotifyBuyerToPay(String oId, Double amount);

//    String buyer1stPayment(String oId, String buyerId);

    String adminNotifySellerToDispatch(String oId);

    String sellerUploadPurchaseInvoice(String oId, String sellerId, MultipartFile file) throws IOException;

    String sellerProcessingOrder(String oId, String sellerId);

    String sellerDispatchedOrder(String oId, String sellerId);

    String sellerAddTransportation(String oId, String sellerId, OrderDTO orderDTO);

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

    List<OrderDTO> buyerGetAllOrders(String buyerId);

    OrderDTO buyerGetOrderDetails(String oId, String buyerId);

    List<OrderDTO> adminGetAllOrders();

    OrderDTO adminGetOrderDetails(String oId);

    String getOIdByQId(String qId);

    List<OrderDTO> sellerGetAllOrders(String sellerId);

    OrderDTO sellerGetOrderDetails(String oId, String sellerId);





}
