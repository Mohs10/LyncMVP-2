package com.example.Lync.Service;

import com.example.Lync.DTO.StandardOperatingProcedureDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StandardOperatingProcedureService {

    public StandardOperatingProcedureDTO createStandardOperatingProcedure(StandardOperatingProcedureDTO dto) ;
    public StandardOperatingProcedureDTO updateStandardOperatingProcedure(Long id, StandardOperatingProcedureDTO dto) ;

    public StandardOperatingProcedureDTO getBuyerStandardOperatingProcedure();
    public StandardOperatingProcedureDTO getSellerStandardOperatingProcedure();
    public String UploadDocumentOfSOPForBuyer(MultipartFile multipartFile) throws IOException ;

    public String UploadDocumentOfSOPForSeller(MultipartFile multipartFile) throws IOException ;



}
