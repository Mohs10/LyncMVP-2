package com.example.Lync.Controller;

import com.example.Lync.DTO.StandardOperatingProcedureDTO;
import com.example.Lync.Service.StandardOperatingProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/standard_operating_procedure")
public class StandardOperatingProcedureController {

    @Autowired
    private StandardOperatingProcedureService standardOperatingProcedureService ;

    // Create Proof of Concept
    @PostMapping("/add/buyer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<StandardOperatingProcedureDTO> createStandardOperatingProcedureForBuyer(@RequestBody StandardOperatingProcedureDTO dto) {
        dto.setForRole("BUYER");
        StandardOperatingProcedureDTO createdDTO = standardOperatingProcedureService.createStandardOperatingProcedure(dto);
        return ResponseEntity.ok(createdDTO);
    }

    @PostMapping("/add/seller")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<StandardOperatingProcedureDTO> createStandardOperatingProcedureForSeller(@RequestBody StandardOperatingProcedureDTO dto) {
        dto.setForRole("SELLER");
        StandardOperatingProcedureDTO createdDTO = standardOperatingProcedureService.createStandardOperatingProcedure(dto);
        return ResponseEntity.ok(createdDTO);
    }

    // Update Proof of Concept
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<StandardOperatingProcedureDTO> updateStandardOperatingProcedure(@PathVariable Long id, @RequestBody StandardOperatingProcedureDTO dto) {
        StandardOperatingProcedureDTO updatedDTO = standardOperatingProcedureService.updateStandardOperatingProcedure(id, dto);
        return ResponseEntity.ok(updatedDTO);
    }

    // Get Buyer Proof of Concept
    @GetMapping("/buyer")
    public ResponseEntity<StandardOperatingProcedureDTO> getBuyerStandardOperatingProcedure() {
        StandardOperatingProcedureDTO buyerPOC = standardOperatingProcedureService.getBuyerStandardOperatingProcedure();
        return ResponseEntity.ok(buyerPOC);
    }

    // Get Seller Proof of Concept
    @GetMapping("/seller")
    public ResponseEntity<StandardOperatingProcedureDTO> getSellerStandardOperatingProcedure() {
        StandardOperatingProcedureDTO sellerPOC = standardOperatingProcedureService.getSellerStandardOperatingProcedure();
        return ResponseEntity.ok(sellerPOC);
    }


    @PostMapping("/upload/buyer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<String> uploadDocumentOfSOPForBuyer(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = standardOperatingProcedureService.UploadDocumentOfSOPForBuyer(file);
            return new ResponseEntity<>("File uploaded successfully. File URL: " + fileUrl, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload/seller")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<String> uploadDocumentOfSOPForSeller(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = standardOperatingProcedureService.UploadDocumentOfSOPForSeller(file);
            return new ResponseEntity<>("File uploaded successfully. File URL: " + fileUrl, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

