package com.example.Lync.ServiceImpl;

import com.example.Lync.Config.MessageConfig;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.*;
import com.example.Lync.Entity.*;
import com.example.Lync.Repository.*;
import com.example.Lync.Service.ProductService;
import com.example.Lync.Service.TestService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
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
    private ProductRepository productRepository;
    @Autowired
    private VarietyRepository varietyRepository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
    public List<OrderInTestingDTO> findBySellerId(String sellerId) {
        return testRepository.findBySellerId(sellerId)
                .stream()
                .map(this::toOrderInTestingDTO)
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
        test.setSamplingFixationDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDate());
        test.setSellerId(inquiry.getSellerUId());
        test.setRequestedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
        test.setTestingStartedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDate());

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
        initialStatus.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
        initialStatus.setAdditionalDetails("Test created for Buyer ID: " + inquiry.getBuyerId());

        // Save the initial TestStatus entry
        testStatusRepository.save(initialStatus);

        Inquiry inquiry1 = inquiryRepository.findByQId(savedTest.getQueryId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id: " + savedTest.getQueryId()));
        inquiry1.setOptedTesting(true);
        inquiryRepository.save(inquiry1);

        String adminMessage = String.format(
                "Testing request has been raised from the Buyer: %s for Query with id: %s",
                savedTest.getBuyerId(),
                savedTest.getQueryId()
        );

        // Send notifications
        sendNotificationBasedOnRoleOrUser(savedTest, "ADMIN", adminMessage);





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

        Test savedTest =  testRepository.save(existingTest);
        // Construct a notification message for the admin
        String buyerMessage = String.format(
                "A testing agency has been assigned for Query ID: %s by Lyncc. " +
                        "Please review the updated testing details.",
                savedTest.getQueryId()
        );

        // Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "BUYER", buyerMessage);

        return savedTest;
    }


    @Override
    public String approveSamplingByAdmin(String testId, Boolean isApproved) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Update the admin approval status
        test.setAdminApprovesTestRequest(isApproved);
       Test savedTest= testRepository.save(test);


        // Construct a notification message for the admin
        String buyerMessage = String.format(
                "Lyncc has " + (isApproved ? "approved" : "rejected") + " the sampling request for Query ID: %s",
                savedTest.getQueryId()
        );

        // Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "BUYER", buyerMessage);

        // Create a TestStatus entry for the approval/rejection
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-002");
        status.setTestId(testId);
        status.setStatusPhase("Phase 1: Fixation of Sampling Date");
        status.setStatusMessage(isApproved ? "Sampling approved by Admin" : "Sampling rejected by Admin");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        Test savedTest = testRepository.save(test);


        // Construct a notification message for the admin
        String adminMessage = String.format(
                "Buyer has " + (isApproved ? "approved" : "rejected") + " the sampling request for Query ID: %s",
                savedTest.getQueryId()
        );

        // Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "ADMIN", adminMessage);

        // Create a TestStatus entry for the approval/rejection
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-003");
        status.setTestId(testId);
        status.setStatusPhase("Phase 1: Fixation of Sampling Date");
        status.setStatusMessage(isApproved ? "Sampling approved by Buyer" : "Sampling rejected by Buyer");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        Test savedTest = testRepository.save(test);
        // Construct a notification message for the admin
        String adminMessage = String.format(
                "Buyer has " + (isApproved ? "approved" : "rejected") + " the sampling request for Query ID: %s",
                savedTest.getQueryId()
        );
        // Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "ADMIN", adminMessage);

        if (isApproved) {
            String sellerMessage = String.format(
                    "You have received a new testing request for Query ID: %s.",
                    savedTest.getQueryId()
            );
            sendNotificationBasedOnRoleOrUser(savedTest, "SELLER", sellerMessage);
        }

        // Create a TestStatus entry for the agreement/rejection
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-004");
        status.setTestId(testId);
        status.setStatusPhase("Phase 1: Fixation of Sampling Date");
        status.setStatusMessage(isApproved ? "Buyer agreed to terms" : "Buyer rejected terms");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        Test savedTest = testRepository.save(test);
        // Construct a notification message for the admin
        String adminMessage = String.format(
                "Seller has " + (isApproved ? "approved" : "rejected") + " the sampling request for Query ID: %s",
                savedTest.getQueryId()
        );
        // Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "ADMIN", adminMessage);

        if (isApproved) {
            String buyerMessage = String.format(
                    "Testing has been initiated for Query ID: %s. Please await further updates.",
                    savedTest.getQueryId()
            );
            sendNotificationBasedOnRoleOrUser(savedTest, "BUYER", buyerMessage);
        }

        // Create a TestStatus entry for the approval/rejection
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-005");
        status.setTestId(testId);
        status.setStatusPhase("Phase 1: Fixation of Sampling Date");
        status.setStatusMessage(isApproved ? "Sampling approved by Seller" : "Sampling rejected by Seller");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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

        Test savedTest = testRepository.save(test);
        // Construct a notification message for the admin
        String adminMessage = String.format(
                "Query ID: %s, Sampling date: %s, Location: %s, Estimated result date: %s",
                savedTest.getQueryId(),
                savedTest.getSamplingDate(),
                savedTest.getSamplingLocation(),
                savedTest.getEstimatedResultDate()
        );


        // Send notifications asynchronously
        sendNotificationBasedOnRoleOrUser(savedTest, "ADMIN", adminMessage);


        // Create a TestStatus entry for the update
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-006");
        status.setTestId(testId);
        status.setStatusPhase("Phase 2: Date of Sampling");
        status.setStatusMessage("Sampling details updated");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        test.setSamplingImagesUrlDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDate());
        test.setSamplingImagesUrlTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalTime().truncatedTo(ChronoUnit.SECONDS));

        // Update the test with the new sampling image URL
        test.setSamplingImagesUrl(path);
        testRepository.save(test);

        // Create a TestStatus entry for the image upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-007");
        status.setTestId(testId);
        status.setStatusPhase("Phase 2: Date of Sampling");
        status.setStatusMessage("Sampling lot image uploaded successfully");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        test.setSealedLotImage1UrlDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDate());
        test.setSealedLotImage1UrlTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalTime().truncatedTo(ChronoUnit.SECONDS));

        // Update the test with the new sealed lot image 1 URL
        test.setSealedLotImage1Url(path);
        testRepository.save(test);

        // Create a TestStatus entry for the sealed lot image upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-008");
        status.setTestId(testId);
        status.setStatusPhase("Phase 2: Date of Sampling");
        status.setStatusMessage("Sealed lot image 1 uploaded successfully");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        test.setSealedLotImage2UrlDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDate());
        test.setSealedLotImage2UrlTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalTime().truncatedTo(ChronoUnit.SECONDS));

        // Update the test with the new sealed lot image 2 URL
        test.setSealedLotImage2Url(path);
        testRepository.save(test);

        // Create a TestStatus entry for the sealed lot image 2 upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-009");
        status.setTestId(testId);
        status.setStatusPhase("Phase 2: Date of Sampling");
        status.setStatusMessage("Sealed lot image 2 uploaded successfully");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        test.setTestingCompletedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDate());
        Test savedTest =  testRepository.save(test);
        // Construct a notification message for the admin
        String buyerMessage = String.format(
                "Test result has been uploaded for test Id : " +  testId
        );

        // Create a TestStatus entry for the test result upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-010");
        status.setTestId(testId);
        status.setStatusPhase("Phase 3: Testing and Results");
        status.setStatusMessage(dto.getTestPassed() ? "Test passed successfully" : "Test failed");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
        status.setAdditionalDetails("Test result uploaded. Test passed: " + dto.getTestPassed() +
                (dto.getTestPassed() ? "" : ", Failure Reason: " + dto.getFailureReason()));

        // Save the TestStatus entry
        testStatusRepository.save(status);

        sendNotificationBasedOnRoleOrUser(savedTest, "BUYER", buyerMessage);
        return "Test results uploaded";
    }


    @Override
    public String uploadTestResultsFile(String testId, MultipartFile multipartFile) throws IOException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with given test Id : " + testId));

        // Set the result upload date
        test.setResultUploadDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());

        // Upload the test report to S3
        String queryId = test.getQueryId();
        String path = s3Service.uploadTestReport(queryId, testId, multipartFile);

        // Update the test with the new test report URL
        test.setTestReportUrl(path);
        Test savedTest =  testRepository.save(test);
        // Construct a notification message for the admin
// Construct a notification message for the admin
        String adminMessage = String.format(
                "Test results are uploaded for Query ID: %s. Please review the uploaded file.",
                savedTest.getQueryId()
        );

// Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "ADMIN", adminMessage);

// Construct a notification message for the buyer
        String buyerMessage = String.format(
                "Test results for Query ID: %s have been uploaded. Please log in to review the results.",
                savedTest.getQueryId()
        );

// Send notification to the buyer
        sendNotificationBasedOnRoleOrUser(savedTest, "BUYER", buyerMessage);


        // Create a TestStatus entry for the test results file upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-011");
        status.setTestId(testId);
        status.setStatusPhase("Phase 3: Testing and Results");
        status.setStatusMessage("Test results file uploaded successfully");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        Test savedTest =  testRepository.save(test);
        // Construct a notification message for the admin
        String adminMessage = String.format(
                " Buyer has " + (isApproved ? "accepted" : "rejected") + " the test result. Query ID: %s" ,
                savedTest.getQueryId()
        );

        // Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "ADMIN", adminMessage);

        // Create a TestStatus entry for the buyer's decision
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-012");
        status.setTestId(testId);
        status.setStatusPhase("Phase 4: Dispatch");
        status.setStatusMessage(isApproved ? "Buyer accepted test result" : "Buyer rejected test result");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        Test savedTest =  testRepository.save(test);
        // Construct a notification message for the admin
        String buyerMessage = String.format(
                "Buyer SOP uploaded for Query ID: %s" +
                        "Please review the uploaded file.",
                savedTest.getQueryId()
        );

        // Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "BUYER", buyerMessage);

        // Create a TestStatus entry for the buyer SOP upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-02-01");
        status.setTestId(testId);
        status.setStatusPhase("Phase 5: Payment");
        status.setStatusMessage("Buyer SOP uploaded successfully");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        Test savedTest =  testRepository.save(test);
        // Construct a notification message for the admin
        String sellerMessage = String.format(
                "Seller SOP uploaded for Query ID: %s" +
                        "Please review the uploaded file.",
                savedTest.getQueryId()
        );

        // Send notification to the admin
        sendNotificationBasedOnRoleOrUser(savedTest, "SELLER", sellerMessage);

        // Create a TestStatus entry for the seller SOP upload
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-03-01");
        status.setTestId(testId);
        status.setStatusPhase("Phase 5: Payment");
        status.setStatusMessage("Seller SOP uploaded successfully");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        test.setPaymentCompletedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());

        // Save the updated test
        testRepository.save(test);

        // Create a TestStatus entry for the payment processing
        TestStatus status = new TestStatus();
        status.setStatusId("STATUS-" + testId + "-019");
        status.setTestId(testId);
        status.setStatusPhase("Phase 5: Payment");
        status.setStatusMessage("Payment processed successfully");
        status.setStatusTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
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
        testDTO.setSamplingImagesUrlDate(test.getSamplingImagesUrlDate());
        testDTO.setSamplingImagesUrlTime(test.getSamplingImagesUrlTime());

        String sealedLotImage1Url = null;
        if (test.getSealedLotImage1Url() != null) {
            sealedLotImage1Url = s3Service.getFiles(test.getSealedLotImage1Url());
        }
        testDTO.setSealedLotImage1Url(sealedLotImage1Url);
        testDTO.setSealedLotImage1UrlDate(test.getSealedLotImage1UrlDate());
        testDTO.setSealedLotImage1UrlTime(test.getSealedLotImage1UrlTime());

        String sealedLotImage2Url = null;
        if (test.getSealedLotImage2Url() != null) {
            sealedLotImage2Url = s3Service.getFiles(test.getSealedLotImage2Url());
        }
        testDTO.setSealedLotImage2Url(sealedLotImage2Url);
        testDTO.setSealedLotImage2UrlDate(test.getSealedLotImage2UrlDate());
        testDTO.setSealedLotImage2UrlTime(test.getSealedLotImage2UrlTime());

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

        Inquiry inquiry = inquiryRepository.findByQId(test.getQueryId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id: " + test.getQueryId()));
        Product product = productRepository.findById(inquiry.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        testDTO.setProductImageUrl(product.getProductImageUrl() != null ? s3Service.getProductImagePresignedUrl(product.getProductImageUrl()) : null);

        return testDTO;
    }




    private OrderInTestingDTO toOrderInTestingDTO(Test test) {
        if (test == null) {
            return null;
        }

        OrderInTestingDTO testDTO = new OrderInTestingDTO();
        Inquiry inquiry = inquiryRepository.findByQId(test.getQueryId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id: " + test.getQueryId()));
        Product product = productRepository.findById(inquiry.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        System.out.println(inquiry);
        // Handle variety name or assign "NA" if not found
        String productVariety = varietyRepository.findVarietyNameByVarietyId(inquiry.getProductVarietyId())
                .orElse("NA");

        testDTO.setTestId(test.getTestId());
        testDTO.setBuyerId(test.getBuyerId());
        testDTO.setSellerId(test.getSellerId());
        testDTO.setQueryId(test.getQueryId());
        testDTO.setProductName(product.getProductName());
        testDTO.setPrice(inquiry.getBuyerFinalPrice());
        testDTO.setVariety(productVariety);
        testDTO.setQuantity(inquiry.getQuantity());
        testDTO.setPrice(inquiry.getSellerFinalPrice());

        String productImageUrl = null;
        if (product.getProductImageUrl() != null) {
            productImageUrl = s3Service.getFiles(product.getProductImageUrl());
        }
        testDTO.setProductImageUrl(productImageUrl);

        return testDTO;
    }





    public void sendNotificationBasedOnRoleOrUser(Test test, String role, String message) {
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage(message);
        // Assuming BuyerId represents the recipient
        if ("BUYER".equalsIgnoreCase(role)) {
            notification.setBuyerId(test.getBuyerId());
        }
        if ("SELLER".equalsIgnoreCase(role)) {
            notification.setSellerId(test.getSellerId());
        }
// Admin logic remains unchanged
        notification.setIsAdmin("ADMIN".equalsIgnoreCase(role));



        notification.setIsRead(false);
        notification.setDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDate());
        notification.setTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalTime().truncatedTo(ChronoUnit.SECONDS));

        // Determine the routing key and topic based on role or userId
        String routingKey;
        String topic;

        if (role != null) {
            switch (role.toUpperCase()) {
                case "ADMIN":
                    routingKey = MessageConfig.ADMIN_ROUTING_KEY;
                    topic = "/topic/notifications";
                    break;
                case "SELLER":
                    routingKey = MessageConfig.SELLER_ROUTING_KEY;
                    topic = "/topic/notifications/seller/"+test.getSellerId();
                    break;
                case "BUYER":
                    routingKey = MessageConfig.BUYER_ROUTING_KEY;
                    topic = "/topic/notifications/buyer/"+test.getBuyerId();
                    break;
                default:
                    routingKey = MessageConfig.DEFAULT_ROUTING_KEY;
                    topic = "/topic/notifications/general";
            }
        } else {
            // Default case if no role is provided
            routingKey = MessageConfig.DEFAULT_ROUTING_KEY;
            topic = "/topic/notifications/general";
        }

        // Send notification to RabbitMQ
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, routingKey, notification);

        // Send notification to WebSocket subscribers
        messagingTemplate.convertAndSend(topic, notification);

        // Save the notification in the repository
        notificationRepository.save(notification);
    }







}
