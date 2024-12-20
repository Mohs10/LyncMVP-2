package com.example.Lync.Controller;

import com.example.Lync.DTO.*;
import com.example.Lync.Entity.Test;
import com.example.Lync.Service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    @Autowired
    private TestService testService;

    // Create or update a test


@GetMapping("/findByTestId/{testId}")
    public TestDTO getTestById(@PathVariable String testId) {
        return testService.getTestById(testId);
    }
    @PostMapping("/initiateTestRequest")
    public ResponseEntity<Test> saveTest(@RequestBody Test test) {
        return ResponseEntity.ok(testService.saveTest(test));
    }

    @PutMapping("/{testId}/sampling/buyer-sop-acceptance")
    public ResponseEntity<String> approveSopByBuyer(@PathVariable String testId, @RequestBody Map<String, Object> approval) {
        Boolean isApproved = (Boolean) approval.get("approved");
        return ResponseEntity.ok(testService.buyerAgreedToTerms(testId, isApproved));
    }


    // Phase 1: Fixation of Sampling Date
    // Admin Approval for Sampling
    @PutMapping("/{testId}/sampling/admin-approval")
    public ResponseEntity<String> approveSamplingByAdmin(@PathVariable String testId, @RequestBody Map<String, Object> approval) {
        Boolean isApproved = (Boolean) approval.get("approved");
        return ResponseEntity.ok(testService.approveSamplingByAdmin(testId, isApproved));
    }


    @PutMapping("/{testId}/sampling/buyer-approval")
    public ResponseEntity<String> approveSamplingByBuyer(@PathVariable String testId, @RequestBody Map<String, Object> approval) {
        Boolean isApproved = (Boolean) approval.get("approved");
        return ResponseEntity.ok(testService.approveSamplingByBuyer(testId, isApproved));
    }

    // Seller Approval for Sampling
    @PutMapping("/{testId}/sampling/seller-approval")
    public ResponseEntity<String> approveSamplingBySeller(@PathVariable String testId, @RequestBody Map<String, Object> approval) {
        Boolean isApproved = (Boolean) approval.get("approved");
        return ResponseEntity.ok(testService.approveSamplingBySeller(testId, isApproved));
    }


    // Phase 2: Date of Sampling
    @PutMapping("/{testId}/sampling/details")
    public ResponseEntity<String> updateSamplingDetails(@PathVariable String testId, @RequestBody TestPhase2DTO dto) {
        return ResponseEntity.ok(testService.updateSamplingDetails(testId, dto));
    }

//    @PutMapping("/{testId}/sampling/results-estimate")
//    public ResponseEntity<String> setResultsEstimate(@PathVariable String testId, @RequestBody Map<String, Object> details) {
//        LocalDate estimatedDate = LocalDate.parse((String) details.get("estimatedDate"));
//        return ResponseEntity.ok(testService.updateDispatchEstimates(testId, estimatedDate, null));
//    }


    @PostMapping("/{testId}/uploadSamplingLotImage")
    public ResponseEntity<String> uploadSamplingLotImage(@PathVariable String testId,
                                                         @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileUrl = testService.uploadSamplingLotImage(testId, file);
                return ResponseEntity.ok("Sampling lot image uploaded successfully at : " + fileUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sampling lot image is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading sampling lot image.");
        }
    }

    @PostMapping("/{testId}/uploadSealedLotImage1")
    public ResponseEntity<String> uploadSealedLotImage1(@PathVariable String testId,
                                                        @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileUrl = testService.uploadSealedLotImage1(testId, file);
                return ResponseEntity.ok("Sealed lot image 1 uploaded successfully at : " + fileUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sealed lot image 1 is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading sealed lot image 1.");
        }
    }

    @PostMapping("/{testId}/uploadSealedLotImage2")
    public ResponseEntity<String> uploadSealedLotImage2(@PathVariable String testId,
                                                        @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileUrl = testService.uploadSealedLotImage2(testId, file);
                return ResponseEntity.ok("Sealed lot image 2 uploaded successfully at : " + fileUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sealed lot image 2 is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading sealed lot image 2.");
        }
    }





    // Phase 3: Testing and Results
    @PutMapping("/{testId}/results")//admin
    public ResponseEntity<String> updateTestResults(@PathVariable String testId, @RequestBody TestPhase3DTO dto) {
        return ResponseEntity.ok(testService.uploadTestResults(testId, dto));
    }

    @PutMapping("/{testId}/results/decision")//for buyer
    public ResponseEntity<String> buyerDecision(@PathVariable String testId, @RequestBody Map<String, Object> decision) {
        Boolean isApproved = (Boolean) decision.get("approved");
        return ResponseEntity.ok(testService.buyerDecision(testId, isApproved));
    }

    @PutMapping("/{testId}/dispatch/estimates")
    public ResponseEntity<String> updateDispatchEstimates(@PathVariable String testId, @RequestBody Map<String, Object> estimates) {
        LocalDate dispatchEstimationDate = LocalDate.parse((String) estimates.get("dispatchEstimationDate"));
        LocalDate arrivalEstimationDate = LocalDate.parse((String) estimates.get("arrivalEstimationDate"));
        return ResponseEntity.ok(testService.updateDispatchEstimates(testId, dispatchEstimationDate, arrivalEstimationDate));
    }

    // Phase 4: Dispatch
    @PutMapping("/{testId}/dispatch/details")
    public ResponseEntity<String> logDispatchDetails(@PathVariable String testId, @RequestBody TestPhase4DTO dto) {
        return ResponseEntity.ok(testService.logDispatchDetails(testId, dto));
    }

    // Phase 5: Payment
    @PutMapping("/{testId}/payment")
    public ResponseEntity<String> processPayment(@PathVariable String testId, @RequestBody TestPhase5DTO dto) {
        return ResponseEntity.ok(testService.processPayment(testId, dto));
    }






    @PostMapping("/{testId}/uploadTestResultsFile")
    public ResponseEntity<String> uploadTestResultsFile(@PathVariable String testId,
                                                        @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileUrl = testService.uploadTestResultsFile(testId, file);
                return ResponseEntity.ok("Test results file uploaded successfully at : " + fileUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test results file is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading test results file.");
        }
    }

    @PostMapping("/{testId}/uploadTestInvoice")
    public ResponseEntity<String> uploadTestInvoice(@PathVariable String testId,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileUrl = testService.uploadTestInvoice(testId, file);
                return ResponseEntity.ok("Test invoice uploaded successfully at : " + fileUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test invoice is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading test invoice.");
        }
    }

    @PostMapping("/{testId}/uploadEwayBill")
    public ResponseEntity<String> uploadEwayBill(@PathVariable String testId,
                                                 @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileUrl = testService.uploadEwayBill(testId, file);
                return ResponseEntity.ok("E-way bill uploaded successfully at : " + fileUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-way bill is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading e-way bill.");
        }
    }

    @PostMapping("/{testId}/uploadDispatchImage1")
    public ResponseEntity<String> uploadDispatchImage1(@PathVariable String testId,
                                                       @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileUrl = testService.uploadDispatchImage1(testId, file);
                return ResponseEntity.ok("Dispatch image 1 uploaded successfully at : " + fileUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dispatch image 1 is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading dispatch image 1.");
        }
    }

    @PostMapping("/{testId}/uploadDispatchImage2")
    public ResponseEntity<String> uploadDispatchImage2(@PathVariable String testId,
                                                       @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileUrl = testService.uploadDispatchImage2(testId, file);
                return ResponseEntity.ok("Dispatch image 2 uploaded successfully at : " + fileUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dispatch image 2 is required.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading dispatch image 2.");
        }
    }















}
