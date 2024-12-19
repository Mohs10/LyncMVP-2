package com.example.Lync.Service;

import com.example.Lync.DTO.*;
import com.example.Lync.Entity.SellerBuyer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InquiryService {

    List<SpecificationDTO> buyerGetsSpecificationsByProductId(Long productId);

    String buyerAddInquiry(InquiryDTO inquiryDTO, String buyerUId) throws Exception; //buyer raise inquiry

    List<InquiryDTO> buyerGetsAllInquiry(String buyerUId);

    List<InquiryDTO> buyerGetsInquiries(String buyerUId);

    InquiryDTO buyerGetsInquiryById(String buyerUId, String qId);

    List<InquiryDTO> adminGetAllInquiry(); //Admin get all inquiry

    InquiryDTO adminGetInquiryByQId(String qId) throws Exception; //Admin get inquiry

    String sendInquiryToSeller(String qId, InquiryDTO inquiryDTO); //Admin send inquiry to seller

    List<SellerProductDTO> sellersSellingProduct(Long productId, Long productFormId, Long productVarietyId, List<String> specificationNames); //Admin gets particular product's sellers.

    List<SellerReceiveInquiryDTO> sellerAllInquiries(String sellerUId); //Seller all inquiries

    List<InquiryDTO> sellerNewInquiries(String sellerUId); //Seller only having status 2 inquiry

//    List<InquiryDTO> buyerGetsStatus1n2Inquiries();

    void buyerRejectsInquiries(String qId, String description, String buyerUId) throws Exception; //Buyer can reject status 1, 2 inquiries

    SellerReceiveInquiryDTO sellerOpenInquiry(Long snId, String sellerUId) throws Exception;

    String sellerRejectQuery(Long snId, String sellerUId) throws Exception;

    String sellerAcceptInquiry(Long snId, String buyerUId) throws Exception;

    String sellerNegotiatePrice(Long snId, String sellerUId, Double amount);

    String adminFinalPriceToSeller(Long snId, Double amount);

    String sellerAcceptAdminPrice(Long snId, String sellerUId);

    String sellerRejectAdminPrice(Long snId, String sellerUId);

    String adminSelectsSeller(Long snId);

    String adminRejectSeller(Long snId);

    String adminQuoteToBuyer(String qId, InquiryDTO inquiryDTO);

    String buyerNegotiatePrice(String qId, String buyerUId, Double amount);

    String adminFinalPriceToBuyer(String qId, Double amount);

    String buyerAcceptQuery(String qId, String buyerUId);

    String buyerRejectedQuery(String qId, String buyerUId);

//    void sellerOrderSample(String qId, SampleOrderDTO sampleOrderDTO) throws Exception;





    String buyerRequestSample(String qId, String buyerUId, SampleOrderDTO sampleOrderDTO) throws Exception;

    List<SampleOrderDTO> buyerGetsAllSampleOrders(String buyerUId);

    SampleOrderDTO buyerGetsSampleOrderById(String soId, String buyerUId);

    List<SampleOrderDTO> adminGetsAllSampleOrders();

    SampleOrderDTO adminGetsSampleOrderById(String soId);

    SampleOrderDTO adminGetsSampleOrderByQId(String qId);

    String adminSendsSampleOrderToSeller(String soId, SampleOrderDTO sampleOrderDTO) throws Exception;

    List<SampleOrderDTO> sellerGetsAllSampleOrders(String sellerUId);

    SampleOrderDTO sellerGetsSampleOrderById(String soId, String sellerUId);
//
//    String sellerApproveSampleOrder(String soId, String sellerUId);
//
//    String sellerDeclineSampleOrder(String soId, String sellerUId);

    String sellerPackagingSample(String soId, String sellerUId);

    String sellerDispatchSampleToAdmin(String soId, String sellerUId, String sellerDispatchSampleToAdmin);

    String adminReceivedSample(String soId);

    String adminProcessingSample(String soId);

    String adminDispatchToBuyer(String soId, String transportationByAdmin);

    String buyerReceivedSample(String soId, String buyerUId);

    String buyerApprovedSample(String soId, String buyerUId);

    String buyerRejectedSample(String soId, String buyerUId);

    List<SellerBuyer> buyersHavingCheque();


    String uploadInvoice(String qId, MultipartFile file) throws IOException;


}
