package com.example.Lync.ServiceImpl;

import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.*;
import com.example.Lync.Entity.Inquiry;
import com.example.Lync.Entity.Test;
import com.example.Lync.Entity.TestStatus;
import com.example.Lync.Repository.InquiryRepository;
import com.example.Lync.Repository.TestRepository;
import com.example.Lync.Repository.TestStatusRepository;
import com.example.Lync.Service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestStatusRepository testStatusRepository;
    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private S3Service s3Service;

    @Override
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    @Override
    public TestDTO getTestById(String testId) {
        return toDTO(testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId)));
    }

    @Override
    public List<TestDTO> getTestsByQueryId(String queryId) {
        return testRepository.findByQueryId(queryId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Test saveTest(Test test) {
        // Fetch the associated Inquiry
        Inquiry inquiry = inquiryRepository.findByQId(test.getQueryId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id: " + test.getQueryId()));

        // Populate Test entity details
        test.setTestId("TEST-" + test.getQueryId() + "-" + System.currentTimeMillis());
        test.setBuyerId(inquiry.getBuyerId());
        test.setSamplingFixationDate(LocalDate.now());
        test.setSellerId(inquiry.getSellerUId());
        test.setRequestedAt(LocalDateTime.now());
        test.setTestingStartedAt(LocalDate.now());

        // Set default agency approval if applicable
        if (test.getIsDefaultAgency()) {
            test.setAdminApprovesTestRequest(true);
        }

        // Save the Test entity
        Test savedTest = testRepository.save(test);

        // Create the initial TestStatus entry
        TestStatus initialStatus = new TestStatus();
        initialStatus.setStatusId("STATUS-" + savedTest.getTestId() + "-001");
        initialStatus.setTestId(savedTest.getTestId());
        initialStatus.setStatusPhase("Request");
        initialStatus.setStatusMessage("Test request initiated successfully.");
        initialStatus.setStatusTimestamp(LocalDateTime.now());
        initialStatus.setAdditionalDetails("Test created for Buyer ID: " + inquiry.getBuyerId());

        // Save the initial TestStatus entry
        testStatusRepository.save(initialStatus);

        return savedTest;
    }

    @Override
    public Test adminAddAgency(Test test, String testId) {
        Test existingTest = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));
        existingTest.setTestingAgencyName(test.getTestingAgencyName());
        existingTest.setTestingParameters(test.getTestingParameters());
        existingTest.setContactPersonEmail(test.getContactPersonEmail());
        existingTest.setContactPersonPhoneNumber(test.getContactPersonPhoneNumber());
        existingTest.setTestingLocation(test.getTestingLocation());

        return testRepository.save(existingTest);
    }


    @Override
    public String approveSamplingByAdmin(String testId, Boolean isApproved) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the admin approval status
        test.setAdminApprovesTestRequest(isApproved);
        testRepository.save(test);

        // Create a TestStatus entry for the approval/rejection
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-002");
        status.setTestId(testId);
        status.setStatusPhase("Phase 1: Fixation of Sampling Date");
        status.setStatusMessage(isApproved ? "Sampling approved by Admin" : "Sampling rejected by Admin");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Admin has " + (isApproved ? "approved" : "rejected") + " the sampling request.");

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return isApproved ? "Sampling approved" : "Sampling rejected";
    }

    @Override
    public String approveSamplingByBuyer(String testId, Boolean isApproved) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the buyer approval status
        test.setBuyerAcceptsTestRequest(isApproved);
        testRepository.save(test);

        // Create a TestStatus entry for the approval/rejection
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-003");
        status.setTestId(testId);
        status.setStatusPhase("Phase 1: Fixation of Sampling Date");
        status.setStatusMessage(isApproved ? "Sampling approved by Buyer" : "Sampling rejected by Buyer");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Buyer has " + (isApproved ? "approved" : "rejected") + " the sampling request.");

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return isApproved ? "Sampling approved" : "Sampling rejected";
    }

    @Override
    public String buyerAgreedToTerms(String testId, Boolean isApproved) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the buyer's agreement to terms
        test.setBuyerAgreedToTerms(isApproved);
        testRepository.save(test);

        // Create a TestStatus entry for the agreement/rejection
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-004");
        status.setTestId(testId);
        status.setStatusPhase("Phase 1: Fixation of Sampling Date");
        status.setStatusMessage(isApproved ? "Buyer agreed to terms" : "Buyer rejected terms");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Buyer has " + (isApproved ? "accepted" : "rejected") + " the terms.");

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return isApproved ? "Terms Accepted" : "Terms rejected";
    }


    @Override
    public String approveSamplingBySeller(String testId, Boolean isApproved) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the seller's approval and agreement to terms
        test.setSellerAcceptsTestRequest(isApproved);
        test.setSellerAgreedToTerms(isApproved);
        testRepository.save(test);

        // Create a TestStatus entry for the approval/rejection
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-005");
        status.setTestId(testId);
        status.setStatusPhase("Phase 1: Fixation of Sampling Date");
        status.setStatusMessage(isApproved ? "Sampling approved by Seller" : "Sampling rejected by Seller");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Seller has " + (isApproved ? "approved" : "rejected") + " the sampling request.");

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return isApproved ? "Sampling approved" : "Sampling rejected";
    }


    @Override
    public String updateSamplingDetails(String testId, TestPhase2DTO dto) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the sampling details
        test.setSamplingDate(dto.getSamplingDate());
        test.setSamplingLocation(dto.getSamplingLocation());
        test.setEstimatedResultDate(dto.getEstimatedResultDate());

        testRepository.save(test);

        // Create a TestStatus entry for the update
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-006");
        status.setTestId(testId);
        status.setStatusPhase("Phase 2: Date of Sampling");
        status.setStatusMessage("Sampling details updated");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Sampling date: " + dto.getSamplingDate() +
                ", Location: " + dto.getSamplingLocation() +
                ", Estimated result date: " + dto.getEstimatedResultDate());

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return "Sampling details updated";
    }


    @Override
    public String uploadSamplingLotImage(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        String queryId = test.getQueryId();

        // Upload the sampling image to S3
        String path = s3Service.uploadTestSamplingImage(queryId, testId, multipartFile);

        // Update the test with the new sampling image URL
        test.setSamplingImagesUrl(path);
        testRepository.save(test);

        // Create a TestStatus entry for the image upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-007");
        status.setTestId(testId);
        status.setStatusPhase("Phase 2: Date of Sampling");
        status.setStatusMessage("Sampling lot image uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Sampling lot image uploaded. Image URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }


    @Override
    public String uploadSealedLotImage1(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        String queryId = test.getQueryId();

        // Upload the sealed lot image 1 to S3
        String path = s3Service.uploadTestSealedLotImage1(queryId, testId, multipartFile);

        // Update the test with the new sealed lot image 1 URL
        test.setSealedLotImage1Url(path);
        testRepository.save(test);

        // Create a TestStatus entry for the sealed lot image upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-008");
        status.setTestId(testId);
        status.setStatusPhase("Phase 2: Date of Sampling");
        status.setStatusMessage("Sealed lot image 1 uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Sealed lot image 1 uploaded.");

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }


    @Override
    public String uploadSealedLotImage2(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        String queryId = test.getQueryId();

        // Upload the sealed lot image 2 to S3
        String path = s3Service.uploadTestSealedLotImage2(queryId, testId, multipartFile);

        // Update the test with the new sealed lot image 2 URL
        test.setSealedLotImage2Url(path);
        testRepository.save(test);

        // Create a TestStatus entry for the sealed lot image 2 upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-009");
        status.setTestId(testId);
        status.setStatusPhase("Phase 2: Date of Sampling");
        status.setStatusMessage("Sealed lot image 2 uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Sealed lot image 2 uploaded. Image URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }


    @Override
    public String uploadTestResults(String testId, TestPhase3DTO dto) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the test results
        test.setTestPassed(dto.getTestPassed());
        test.setFailureReason(dto.getFailureReason());
        test.setTestingCompletedAt(LocalDate.now());
        testRepository.save(test);

        // Create a TestStatus entry for the test result upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-010");
        status.setTestId(testId);
        status.setStatusPhase("Phase 3: Testing and Results");
        status.setStatusMessage(dto.getTestPassed() ? "Test passed successfully" : "Test failed");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Test result uploaded. Test passed: " + dto.getTestPassed() +
                (dto.getTestPassed() ? "" : ", Failure Reason: " + dto.getFailureReason()));

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return "Test results uploaded";
    }


    @Override
    public String uploadTestResultsFile(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Set the result upload date
        test.setResultUploadDate(LocalDateTime.now());

        // Upload the test report to S3
        String queryId = test.getQueryId();
        String path = s3Service.uploadTestReport(queryId, testId, multipartFile);

        // Update the test with the new test report URL
        test.setTestReportUrl(path);
        testRepository.save(test);

        // Create a TestStatus entry for the test results file upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-011");
        status.setTestId(testId);
        status.setStatusPhase("Phase 3: Testing and Results");
        status.setStatusMessage("Test results file uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Test results file uploaded. File URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }


    @Override
    public String buyerDecision(String testId, Boolean isApproved) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the buyer's decision on the test result
        test.setBuyerAcceptsTestRequest(isApproved);
        testRepository.save(test);

        // Create a TestStatus entry for the buyer's decision
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-012");
        status.setTestId(testId);
        status.setStatusPhase("Phase 4: Dispatch");
        status.setStatusMessage(isApproved ? "Buyer accepted test result" : "Buyer rejected test result");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Buyer has " + (isApproved ? "accepted" : "rejected") + " the test result.");

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return isApproved ? "Test Result Accepted" : "Test Result rejected";
    }


    @Override
    public String updateDispatchEstimates(String testId, LocalDate dispatchEstimationDate, LocalDate arrivalEstimationDate) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the dispatch and arrival estimation dates
        test.setDispatchEstimationDate(dispatchEstimationDate);
        test.setArrivalEstimationDate(arrivalEstimationDate);
        testRepository.save(test);

        // Create a TestStatus entry for the dispatch and arrival estimates update
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-013");
        status.setTestId(testId);
        status.setStatusPhase("Phase 4: Dispatch");
        status.setStatusMessage("Dispatch and arrival estimates updated");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Dispatch estimation date: " + dispatchEstimationDate + ", Arrival estimation date: " + arrivalEstimationDate);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return "Dispatch and arrival estimates updated";
    }


    @Override
    public String logDispatchDetails(String testId, TestPhase4DTO dto) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the dispatch details
        test.setDispatchDate(dto.getDispatchDate());
        test.setVehicleWeight(dto.getVehicleWeight());
        testRepository.save(test);

        // Create a TestStatus entry for the dispatch details logging
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-014");
        status.setTestId(testId);
        status.setStatusPhase("Phase 4: Dispatch");
        status.setStatusMessage("Dispatch details logged successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Dispatch date: " + dto.getDispatchDate() + ", Vehicle weight: " + dto.getVehicleWeight());

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return "Dispatch details logged";
    }


    @Override
    public String uploadDispatchImage1(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));
        String queryId = test.getQueryId();

        // Upload the dispatch image 1 to S3
        String path = s3Service.uploadDispatchImage1(queryId, testId, multipartFile);

        // Update the test with the new dispatch image 1 URL
        test.setDispatchImage1Url(path);
        testRepository.save(test);

        // Create a TestStatus entry for the dispatch image 1 upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-015");
        status.setTestId(testId);
        status.setStatusPhase("Phase 4: Dispatch");
        status.setStatusMessage("Dispatch image 1 uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Dispatch image 1 uploaded. Image URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }


    @Override
    public String uploadDispatchImage2(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));
        String queryId = test.getQueryId();

        // Upload the dispatch image 2 to S3
        String path = s3Service.uploadDispatchImage2(queryId, testId, multipartFile);

        // Update the test with the new dispatch image 2 URL
        test.setDispatchImage2Url(path);
        testRepository.save(test);

        // Create a TestStatus entry for the dispatch image 2 upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-016");
        status.setTestId(testId);
        status.setStatusPhase("Phase 4: Dispatch");
        status.setStatusMessage("Dispatch image 2 uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Dispatch image 2 uploaded. Image URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }



    public String uploadTestInvoice(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));
        String queryId = test.getQueryId();

        // Upload the test invoice to S3
        String path = s3Service.uploadTestInvoice(queryId, testId, multipartFile);

        // Update the test with the new test invoice URL
        test.setTestInvoiceUrl(path);
        testRepository.save(test);

        // Create a TestStatus entry for the test invoice upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-017");
        status.setTestId(testId);
        status.setStatusPhase("Phase 4: Dispatch");
        status.setStatusMessage("Test invoice uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Test invoice uploaded. Invoice URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }


    public String uploadEwayBill(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));
        String queryId = test.getQueryId();

        // Upload the e-way bill to S3
        String path = s3Service.uploadEwayBill(queryId, testId, multipartFile);

        // Update the test with the new e-way bill URL
        test.setEwayBillUrl(path);
        testRepository.save(test);

        // Create a TestStatus entry for the e-way bill upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-018");
        status.setTestId(testId);
        status.setStatusPhase("Phase 4: Dispatch");
        status.setStatusMessage("E-way bill uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("E-way bill uploaded. E-way Bill URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }



    public String uploadBuyerSOP(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));
        String queryId = test.getQueryId();

        // Upload the SOP file for the buyer
        String path = s3Service.uploadBuyerSOP(queryId, testId, multipartFile);

        // Update the test with the SOP URL
        test.setSopForBuyerUrl(path);
        testRepository.save(test);

        // Create a TestStatus entry for the buyer SOP upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-02-01");
        status.setTestId(testId);
        status.setStatusPhase("Phase 5: Payment");
        status.setStatusMessage("Buyer SOP uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Buyer SOP uploaded. SOP URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }




    public String uploadSellerSOP(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));
        String queryId = test.getQueryId();

        // Upload the SOP file for the seller
        String path = s3Service.uploadSellerSOP(queryId, testId, multipartFile);

        // Update the test with the SOP URL
        test.setSopForSellerUrl(path);
        testRepository.save(test);

        // Create a TestStatus entry for the seller SOP upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-03-01");
        status.setTestId(testId);
        status.setStatusPhase("Phase 5: Payment");
        status.setStatusMessage("Seller SOP uploaded successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Seller SOP uploaded. SOP URL: " + path);

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return path;
    }

   public String getBuyerTestSOP(String testId)
    {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        return s3Service.getFiles(test.getSopForBuyerUrl());

    }



   public String getSellerTestSOP(String testId)
    {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        return s3Service.getFiles(test.getSopForSellerUrl());

    }



    @Override
    public String processPayment(String testId, TestPhase5DTO dto) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Set payment completed timestamp
        test.setPaymentCompletedAt(LocalDateTime.now());

        // Save the updated test
        testRepository.save(test);

        // Create a TestStatus entry for the payment processing
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-019");
        status.setTestId(testId);
        status.setStatusPhase("Phase 5: Payment");
        status.setStatusMessage("Payment processed successfully");
        status.setStatusTimestamp(LocalDateTime.now());
        status.setAdditionalDetails("Payment completed for test. Payment processed at: " + test.getPaymentCompletedAt());

        // Save the TestStatus entry
        testStatusRepository.save(status);

        return "Payment processed successfully";
    }





    private TestDTO toDTO(Test test) {
        if (test == null) {
            return null;
        }

        TestDTO testDTO = new TestDTO();
        testDTO.setTestId(test.getTestId());
        testDTO.setBuyerId(test.getBuyerId());
        testDTO.setSellerId(test.getSellerId());
        testDTO.setRequestedAt(test.getRequestedAt());
        testDTO.setQueryId(test.getQueryId());
        testDTO.setTestRequestDate(test.getTestRequestDate());
        testDTO.setReportUploadedAt(test.getReportUploadedAt());
        testDTO.setTestCost(test.getTestCost());
        testDTO.setApprovedBy(test.getApprovedBy());
        testDTO.setTestingStartedAt(test.getTestingStartedAt());
        testDTO.setTestingCompletedAt(test.getTestingCompletedAt());

        // Transform URLs
        String testInvoiceUrl = null;
        if (test.getTestInvoiceUrl() != null) {
            testInvoiceUrl = s3Service.getFiles(test.getTestInvoiceUrl());
        }
        testDTO.setTestInvoiceUrl(testInvoiceUrl);

        String samplingImagesUrl = null;
        if (test.getSamplingImagesUrl() != null) {
            samplingImagesUrl = s3Service.getFiles(test.getSamplingImagesUrl());
        }
        testDTO.setSamplingImagesUrl(samplingImagesUrl);

        String sealedLotImage1Url = null;
        if (test.getSealedLotImage1Url() != null) {
            sealedLotImage1Url = s3Service.getFiles(test.getSealedLotImage1Url());
        }
        testDTO.setSealedLotImage1Url(sealedLotImage1Url);

        String sealedLotImage2Url = null;
        if (test.getSealedLotImage2Url() != null) {
            sealedLotImage2Url = s3Service.getFiles(test.getSealedLotImage2Url());
        }
        testDTO.setSealedLotImage2Url(sealedLotImage2Url);

        String testReportUrl = null;
        if (test.getTestReportUrl() != null) {
            testReportUrl = s3Service.getFiles(test.getTestReportUrl());
        }
        testDTO.setTestReportUrl(testReportUrl);

        String dispatchImage1Url = null;
        if (test.getDispatchImage1Url() != null) {
            dispatchImage1Url = s3Service.getFiles(test.getDispatchImage1Url());
        }
        testDTO.setDispatchImage1Url(dispatchImage1Url);

        String dispatchImage2Url = null;
        if (test.getDispatchImage2Url() != null) {
            dispatchImage2Url = s3Service.getFiles(test.getDispatchImage2Url());
        }
        testDTO.setDispatchImage2Url(dispatchImage2Url);

        String invoiceUrl = null;
        if (test.getInvoiceUrl() != null) {
            invoiceUrl = s3Service.getFiles(test.getInvoiceUrl());
        }
        testDTO.setInvoiceUrl(invoiceUrl);

        String ewayBillUrl = null;
        if (test.getEwayBillUrl() != null) {
            ewayBillUrl = s3Service.getFiles(test.getEwayBillUrl());
        }
        testDTO.setEwayBillUrl(ewayBillUrl);


        String buyerSopUrl = null;
        if (test.getSopForBuyerUrl() != null) {
            buyerSopUrl = s3Service.getFiles(test.getSopForBuyerUrl());
        }
        testDTO.setSopForBuyerUrl(buyerSopUrl);


        String sellerSopUrl = null;
        if (test.getSopForSellerUrl() != null) {
            sellerSopUrl = s3Service.getFiles(test.getSopForSellerUrl());
        }
        testDTO.setSopForSellerUrl(sellerSopUrl);

        testDTO.setIsDefaultAgency(test.getIsDefaultAgency());
        testDTO.setTestingAgencyName(test.getTestingAgencyName());
        testDTO.setTestingParameters(test.getTestingParameters());
        testDTO.setContactPersonEmail(test.getContactPersonEmail());
        testDTO.setContactPersonPhoneNumber(test.getContactPersonPhoneNumber());
        testDTO.setTestingLocation(test.getTestingLocation());
        testDTO.setSamplingFixationDate(test.getSamplingFixationDate());
        testDTO.setSamplingAcceptanceDeadline(test.getSamplingAcceptanceDeadline());
        testDTO.setSamplingDate(test.getSamplingDate());
        testDTO.setSamplingLocation(test.getSamplingLocation());
        testDTO.setEstimatedResultDate(test.getEstimatedResultDate());
        testDTO.setTestPassed(test.getTestPassed());
        testDTO.setFailureReason(test.getFailureReason());
        testDTO.setResultUploadDate(test.getResultUploadDate());
        testDTO.setDispatchEstimationDate(test.getDispatchEstimationDate());
        testDTO.setArrivalEstimationDate(test.getArrivalEstimationDate());
        testDTO.setDispatchDate(test.getDispatchDate());
        testDTO.setVehicleWeight(test.getVehicleWeight());
        testDTO.setPayment(test.getPayment());
        testDTO.setPaymentCompletedAt(test.getPaymentCompletedAt());
        testDTO.setBuyerAgreedToTerms(test.getBuyerAgreedToTerms());
        testDTO.setSellerAgreedToTerms(test.getSellerAgreedToTerms());
        testDTO.setAdminApprovesTestRequest(test.getAdminApprovesTestRequest());
        testDTO.setSellerAcceptsTestRequest(test.getSellerAcceptsTestRequest());
        testDTO.setBuyerAcceptsTestRequest(test.getBuyerAcceptsTestRequest());
        testDTO.setTestResultRejected(test.getTestResultRejected());

        // Fetch and set status list
        List<TestStatus> statusList = testStatusRepository.findByTestId(test.getTestId());
        testDTO.setStatusList(statusList);

        return testDTO;
    }














}
