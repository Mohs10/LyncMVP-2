package com.example.Lync.Service;

import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SampleOrderDTO;

import java.util.List;

public interface InquiryService {
    void addInquiry(InquiryDTO inquiryDTO); //buyer raise inquiry

    List<InquiryDTO> getAllInquiries(); //Admin get all inquiry

    InquiryDTO getInquiryByQId(String qId) throws Exception; //Admin get inquiry

    void sendInquiryToSeller(String qId, InquiryDTO inquiryDTO); //Admin send inquiry to seller

    List<InquiryDTO> sellerAllInquiries(); //Seller all inquiries

    List<InquiryDTO> sellerNewInquiries(); //Seller only having status 2 inquiry

    InquiryDTO sellerOpenInquiry(String qId) throws Exception;

    void sellerAcceptQuery(String qId, String description) throws Exception;

    void sellerRejectQuery(String qId, String description) throws Exception;

    void sellerOrderSample(String qId, SampleOrderDTO sampleOrderDTO) throws Exception;
}
