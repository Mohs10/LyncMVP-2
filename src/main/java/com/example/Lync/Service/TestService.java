package com.example.Lync.Service;


import com.example.Lync.DTO.*;
import com.example.Lync.Entity.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface TestService {
    List<Test> getAllTests();
    public TestDTO getTestById(String testId) ;
    List<TestDTO> getTestsByQueryId(String queryId);
    public List<OrderInTestingDTO> findBySellerId(String sellerId) ;

    Test saveTest(Test test);
    Test adminAddAgency(Test test, String testId);
    String approveSamplingByAdmin(String testId, Boolean isApproved);
    public String approveSamplingByBuyer(String testId, Boolean isApproved) ;
    public String buyerAgreedToTerms(String testId, Boolean isApproved) ;
    String approveSamplingBySeller(String testId, Boolean isApproved);
    String updateSamplingDetails(String testId, TestPhase2DTO dto);
    String uploadSamplingLotImage(String testId, MultipartFile multipartFile) throws IOException;
    String uploadSealedLotImage1(String testId, MultipartFile multipartFile) throws IOException;
    String uploadSealedLotImage2(String testId, MultipartFile multipartFile) throws IOException;
    String uploadTestResults(String testId, TestPhase3DTO dto);
    String uploadTestResultsFile(String testId, MultipartFile multipartFile) throws IOException;
    String uploadTestInvoice(String testId, MultipartFile multipartFile) throws IOException ;
    String uploadEwayBill(String testId, MultipartFile multipartFile) throws IOException ;

    String buyerDecision(String testId, Boolean isApproved);
    String updateDispatchEstimates(String testId, LocalDate dispatchEstimationDate, LocalDate arrivalEstimationDate);
    String logDispatchDetails(String testId, TestPhase4DTO dto);
    String uploadDispatchImage1(String testId, MultipartFile multipartFile) throws IOException;
    String uploadDispatchImage2(String testId, MultipartFile multipartFile) throws IOException;
    public String uploadSellerSOP(String testId, MultipartFile multipartFile) throws IOException ;
    public String uploadBuyerSOP(String testId, MultipartFile multipartFile) throws IOException ;
    String getBuyerTestSOP(String testId);
    String getSellerTestSOP(String testId);




    String processPayment(String testId, TestPhase5DTO dto);

    public void sendNotificationBasedOnRoleOrUser(Test test, String role, String message) ;

    }

