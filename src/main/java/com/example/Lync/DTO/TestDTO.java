package com.example.Lync.DTO;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.Lync.Entity.TestStatus;
import lombok.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TestDTO {

    private String testId; // Format: "TEST-YYYY-XXXXX"
    private String buyerId; // Buyer ID
    private String sellerId; // Seller ID
    private LocalDateTime requestedAt; // Timestamp for when the request was made
    private String queryId; // Query ID
    private LocalDate testRequestDate; // Scheduled date for the test
    private LocalDateTime reportUploadedAt; // Timestamp for when the test report was uploaded
    private Double testCost; // Cost of the test
    private String approvedBy; // Admin ID or authority approving the test
    private LocalDate testingStartedAt; // Timestamp for when testing started
    private LocalDate testingCompletedAt; // Timestamp for when testing completed
    private String testInvoiceUrl; // URL for the test invoice
    private Boolean isDefaultAgency = true; // True if default agency is used
    private String testingAgencyName; // Name of the Testing Agency
    private String testingParameters; // Parameters being tested
    private String contactPersonEmail; // Contact person Email
    private String contactPersonPhoneNumber; // Contact person Phone number
    private String testingLocation; // Location where testing is conducted
    private LocalDate samplingFixationDate; // Sampling fixation date by seller/admin
    private LocalDate samplingAcceptanceDeadline; // Deadline for buyer's acceptance
    private LocalDate samplingDate; // Actual sampling date
    private String samplingLocation; // Location of the lot
    private String samplingImagesUrl; // URL for sampling images
    private String sealedLotImage1Url; // URL for sealed lot images with 3rd party label
    private String sealedLotImage2Url; // URL for sealed lot images with 3rd party label
    private LocalDate estimatedResultDate; // Estimated result date
    private String testReportUrl; // URL of the test report
    private Boolean testPassed = false; // Indicates whether the test passed
    private String failureReason; // Reason for test failure
    private LocalDateTime resultUploadDate; // Timestamp when the results were uploaded
    private LocalDate dispatchEstimationDate; // Estimated dispatch date
    private LocalDate arrivalEstimationDate; // Estimated arrival date
    private LocalDate dispatchDate; // Dispatch date
    private String dispatchImage1Url; // URL for dispatch (loading and sealing) images
    private String dispatchImage2Url; // URL for dispatch (loading and sealing) images
    private Double vehicleWeight; // Weight of the vehicle
    private String invoiceUrl; // URL for the invoice
    private String ewayBillUrl; // URL for the e-way bill
    private String payment; // Status of Payment Phase
    private LocalDateTime paymentCompletedAt; // Timestamp when payment was completed
    private Boolean buyerAgreedToTerms = false; // Buyer has accepted Terms and Conditions
    private Boolean sellerAgreedToTerms = false; // Seller has accepted Terms and Conditions
    private Boolean adminApprovesTestRequest = false; // Approval status by admin
    private Boolean sellerAcceptsTestRequest = false; // Seller has accepted Test Request
    private Boolean buyerAcceptsTestRequest = false; // Buyer accepts Test Report
    private Boolean testResultRejected = false; // Buyer rejects Test Report
    private List<TestStatus> statusList;
}

