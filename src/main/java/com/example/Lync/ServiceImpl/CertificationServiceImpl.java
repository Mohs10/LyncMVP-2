package com.example.Lync.ServiceImpl;

import com.example.Lync.Entity.Certification;
import com.example.Lync.Repository.CertificationRepository;
import com.example.Lync.Service.CertificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;

    public CertificationServiceImpl(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }


    @Override
    public String addCertification(Certification certification) {
        if (certificationRepository.existsByCertificationName(certification.getCertificationName())) {
            return "Certification with this name already exists";
        }
        certificationRepository.save(certification);
        return "Certification added successfully";
    }

    @Override
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    @Override
    public Certification getCertificationByName(String certificationName) {
        return certificationRepository.findByCertificationName(certificationName);
    }
}

