package com.example.Lync.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity class representing a Test request, its lifecycle, and related details.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class Test {

    // Primary Identifier
    @Id
    private String testId; // Format: "TEST-YYYY-XXXXX"

    // Request Information
    private String buyerId; // Buyer ID (replaces requestedBy)
    private String sellerId; // Seller ID associated with the test request
    private LocalDateTime requestedAt; // Timestamp for when the request was made
    private String queryId; // Query ID

    // Test Request Details
    private LocalDate testRequestDate; // Scheduled date for the test
    private LocalDateTime reportUploadedAt; // Timestamp for when the test report was uploaded
    private Double testCost; // Cost of the test
    private String approvedBy; // Admin ID or authority approving the test

    // Testing Lifecycle Timestamps
    private LocalDate  testingStartedAt; // Timestamp for when testing started
    private LocalDate testingCompletedAt; // Timestamp for when testing completed

    // Invoice Details
    private String testInvoiceUrl; // URL for the test invoice (can be filled by admin or buyer)

    // Testing Agency Details
    private Boolean isDefaultAgency = true; // True if default agency is used
    private String testingAgencyName; // Name of the Testing Agency
    private String testingParameters; // Parameters being tested

    private String contactPersonEmail; // Contact person Email
    private String contactPersonPhoneNumber; // Contact person Phone number

    @Column(columnDefinition = "TEXT")
    private String testingLocation; // Location where testing is LocalDateTimeconducted

    // Phase 1: Fixation of Sampling Date


    private LocalDate samplingFixationDate; // Sampling fixation date by seller/admin
    private LocalDate samplingAcceptanceDeadline; // Deadline for buyer's acceptance



    // Phase 2: Sampling Details


    private LocalDate samplingDate; // Actual sampling date (when testing started by 3rd party)
    @Column(columnDefinition = "TEXT")
    private String samplingLocation; // Location of the lot
    private String samplingImagesUrl; // URL for sampling images
    private LocalDate samplingImagesUrlDate; // Update time samplingImagesUrlDate
    private LocalTime samplingImagesUrlTime;
    private String sealedLotImage1Url; // URL for sealed lot images with 3rd party label
    private LocalDate sealedLotImage1UrlDate; // Update time sealedLotImage1UrlDate
    private LocalTime sealedLotImage1UrlTime;
    private String sealedLotImage2Url; // URL for sealed lot images with 3rd party label
    private LocalDate sealedLotImage2UrlDate; // Update time sealedLotImage2UrlDate
    private LocalTime sealedLotImage2UrlTime;

    private LocalDate estimatedResultDate; // Estimated result date (after 10 days)

    // Phase 3: Testing and Results


    private String testReportUrl; // URL of the test report (if available)
    private Boolean testPassed= false; // Indicates whether the test passed
    private String failureReason; // Reason for test failure (if any)
    private LocalDateTime resultUploadDate; // Timestamp when the results were uploaded
    private LocalDate dispatchEstimationDate; // Estimated dispatch date (if passed)
    private LocalDate arrivalEstimationDate; // Estimated arrival date (7 days from dispatch)

    // Phase 4: Dispatch Details


    private LocalDate dispatchDate; // Dispatch date
    private String dispatchImage1Url; // URL for dispatch (loading and sealing) images
    private String dispatchImage2Url; // URL for dispatch (loading and sealing) images

    private Double vehicleWeight; // Weight of the vehicle
    private String invoiceUrl; // URL for the invoice
    private String ewayBillUrl; // URL for the invoice

    // Phase 5: Payment Details
    private String payment ; // Status of Payment Phase

    private LocalDateTime paymentCompletedAt; // Timestamp when payment was completed

    // Agreement and Approval Flags


    private String sopForSellerUrl;
    private String sopForBuyerUrl;


    private Boolean buyerAgreedToTerms = false; // Buyer has accepted Terms and Conditions (T&C)
    private Boolean sellerAgreedToTerms = false; // Seller has accepted Terms and Conditions (T&C)
    private Boolean adminApprovesTestRequest = false; // Approval status by admin
    private Boolean sellerAcceptsTestRequest = false; // Seller has accepted Test Request
    private Boolean buyerAcceptsTestRequest = false; // Buyer accepts Test Report
    private Boolean testResultRejected = false; // Buyer accepts Test Report

}
