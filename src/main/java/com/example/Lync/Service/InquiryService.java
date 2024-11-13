package com.example.Lync.Service;

import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SampleOrderDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.DTO.SellerReceiveInquiryDTO;

import java.util.List;

public interface InquiryService {

    String buyerAddInquiry(InquiryDTO inquiryDTO, String buyerUId) throws Exception; //buyer raise inquiry

    List<InquiryDTO> buyerGetsAllInquiry(String buyerUId);

    InquiryDTO buyerGetsInquiryById(String buyerUId, String qId);

    List<InquiryDTO> adminGetAllInquiry(); //Admin get all inquiry

    InquiryDTO adminGetInquiryByQId(String qId) throws Exception; //Admin get inquiry

    String sendInquiryToSeller(String qId, InquiryDTO inquiryDTO); //Admin send inquiry to seller

    List<SellerProductDTO> sellersSellingProduct(Long productId, Long productFormId, Long productVarietyId); //Admin gets particular product's sellers.

    List<SellerReceiveInquiryDTO> sellerAllInquiries(String sellerUId); //Seller all inquiries

    List<InquiryDTO> sellerNewInquiries(String sellerUId); //Seller only having status 2 inquiry

//    List<InquiryDTO> buyerGetsStatus1n2Inquiries();

    void buyerRejectsInquiries(String qId, String description, String buyerUId) throws Exception; //Buyer can reject status 1, 2 inquiries

    SellerReceiveInquiryDTO sellerOpenInquiry(Long snId) throws Exception;

    String sellerNegotiatePrice(Long snId, String sellerUId, Double amount);

    String adminFinalPriceToSeller(Long snId, Double amount);

    String sellerAcceptInquiry(Long snId, String buyerUId) throws Exception;

    String adminSelectsSeller(Long snId);

    String adminQuoteToBuyer(String qId, InquiryDTO inquiryDTO);

    String buyerNegotiatePrice(String qId, String buyerUId, Double amount);

    String adminFinalPriceToBuyer(String qId, Double amount);

    String buyerAcceptQuery(String qId, String buyerUId);

    void sellerRejectQuery(String qId, String description) throws Exception;

    void sellerOrderSample(String qId, SampleOrderDTO sampleOrderDTO) throws Exception;
}
