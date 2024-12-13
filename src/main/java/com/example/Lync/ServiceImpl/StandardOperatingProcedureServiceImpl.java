package com.example.Lync.ServiceImpl;


import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.StandardOperatingProcedureDTO;
import com.example.Lync.Entity.StandardOperatingProcedure;
import com.example.Lync.Repository.StandardOperatingProcedureRepository;
import com.example.Lync.Service.StandardOperatingProcedureService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardOperatingProcedureServiceImpl implements StandardOperatingProcedureService {

    private final StandardOperatingProcedureRepository repository;
    private final S3Service s3Service;
    public StandardOperatingProcedureServiceImpl(StandardOperatingProcedureRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
    }

    public List<StandardOperatingProcedureDTO> getAllStandardOperatingProcedures() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StandardOperatingProcedureDTO getSellerStandardOperatingProcedure()
    {
        StandardOperatingProcedure standardOperatingProcedure = repository.findByForRole("SELLER") .orElseThrow(() -> new EntityNotFoundException("Proof of Concept not found for SELLER"));

        return mapToDTO(standardOperatingProcedure);
    }

    public StandardOperatingProcedureDTO getBuyerStandardOperatingProcedure()
    {
        StandardOperatingProcedure standardOperatingProcedure = repository.findByForRole("BUYER") .orElseThrow(() -> new EntityNotFoundException("Proof of Concept not found for BUYER"));

        return mapToDTO(standardOperatingProcedure);
    }

    public StandardOperatingProcedureDTO createStandardOperatingProcedure(StandardOperatingProcedureDTO dto) {
        // Check if there are already 2 or more records in the repository
        if (repository.findAll().size() >= 2) {
            throw new IllegalStateException("Cannot add more than 2 StandardOperatingProcedure entries.");
        }
        dto.setDate(LocalDate.now());
        // Map DTO to entity and save it
        StandardOperatingProcedure poc = mapToEntity(dto);
        StandardOperatingProcedure savedPoc = repository.save(poc);

        // Map the saved entity back to DTO and return it
        return mapToDTO(savedPoc);
    }



    public StandardOperatingProcedureDTO getStandardOperatingProcedureById(Long id) {
        StandardOperatingProcedure poc = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proof of Concept not found with ID: " + id));
        return mapToDTO(poc);
    }

    public StandardOperatingProcedureDTO updateStandardOperatingProcedure(Long id, StandardOperatingProcedureDTO dto) {
        StandardOperatingProcedure existingPoc = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proof of Concept not found with ID: " + id));

        existingPoc.setDate(dto.getDate());
        existingPoc.setContent(dto.getContent());
        existingPoc.setDocumentPath(dto.getDocumentPath());


        StandardOperatingProcedure updatedPoc = repository.save(existingPoc);
        return mapToDTO(updatedPoc);
    }

    public String UploadDocumentOfSOPForBuyer(MultipartFile multipartFile) throws IOException {

        String key =s3Service.uploadBuyerSOP(multipartFile);
        StandardOperatingProcedure standardOperatingProcedure = repository.findByForRole("BUYER") .orElseThrow(() -> new EntityNotFoundException("Proof of Concept not found for BUYER"));

        StandardOperatingProcedure updatingStandardOperatingProcedure= repository.findById(standardOperatingProcedure.getId()) .orElseThrow(() -> new EntityNotFoundException("Proof of Concept not found for Id"));
        updatingStandardOperatingProcedure.setDocumentPath(key);
        repository.save(updatingStandardOperatingProcedure);

        return key;
    }

    public String UploadDocumentOfSOPForSeller(MultipartFile multipartFile) throws IOException {

        String key =s3Service.uploadSellerSOP(multipartFile);
        StandardOperatingProcedure standardOperatingProcedure = repository.findByForRole("SELLER") .orElseThrow(() -> new EntityNotFoundException("Proof of Concept not found for BUYER"));
        StandardOperatingProcedure updatingStandardOperatingProcedure= repository.findById(standardOperatingProcedure.getId()) .orElseThrow(() -> new EntityNotFoundException("Proof of Concept not found for Id"));
        updatingStandardOperatingProcedure.setDocumentPath(key);
        repository.save(updatingStandardOperatingProcedure);

        return key;
    }

    public void deleteStandardOperatingProcedure(Long id) {
        repository.deleteById(id);
    }

    private StandardOperatingProcedure mapToEntity(StandardOperatingProcedureDTO dto) {
        return StandardOperatingProcedure.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .content(dto.getContent())
                .documentPath(dto.getDocumentPath())
                .forRole(dto.getForRole())
                .build();
    }

    private StandardOperatingProcedureDTO mapToDTO(StandardOperatingProcedure entity) {
        String documentPath = null; // Initialize to avoid unassigned variable in case of missing conditions.


        if(entity.getDocumentPath()!=null)
        {
            if ("BUYER".equalsIgnoreCase(entity.getForRole())) {
                documentPath = s3Service.getBuyerSOP(entity.getDocumentPath());
            } else if ("SELLER".equalsIgnoreCase(entity.getForRole())) {
                documentPath = s3Service.getSellerSOP(entity.getDocumentPath());
            }
        }

        return StandardOperatingProcedureDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .content(entity.getContent())
                .documentPath(documentPath) // Ensure this is set based on role
                .forRole(entity.getForRole())
                .build();
    }

}
