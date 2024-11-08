package com.example.Lync.Service;

import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SampleOrderDTO;
import com.example.Lync.DTO.SellerProductDTO;

import java.util.List;

public interface InquiryService {
    void addInquiry(InquiryDTO inquiryDTO, String buyerUId) throws Exception; //buyer raise inquiry

    List<InquiryDTO> getAllInquiries(); //Admin get all inquiry

    InquiryDTO getInquiryByQId(String qId) throws Exception; //Admin get inquiry

    String sendInquiryToSeller(String qId, InquiryDTO inquiryDTO); //Admin send inquiry to seller

    List<SellerProductDTO> sellersSellingProduct(Long productId); //Admin gets particular product's sellers.

    List<InquiryDTO> sellerAllInquiries(String sellerUId); //Seller all inquiries

    List<InquiryDTO> sellerNewInquiries(String sellerUId); //Seller only having status 2 inquiry

//    List<InquiryDTO> buyerGetsStatus1n2Inquiries();

    void buyerRejectsInquiries(String qId, String description, String buyerUId) throws Exception; //Buyer can reject status 1, 2 inquiries

    InquiryDTO sellerOpenInquiry(String qId) throws Exception;

    void sellerAcceptQuery(String qId, String description) throws Exception;

    void sellerRejectQuery(String qId, String description) throws Exception;

    void sellerOrderSample(String qId, SampleOrderDTO sampleOrderDTO) throws Exception;
}
