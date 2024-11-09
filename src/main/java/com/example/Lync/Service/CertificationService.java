package com.example.Lync.Service;

import com.example.Lync.Entity.Certification;

import java.util.List;

public interface CertificationService {
    String addCertification(Certification certification);
    List<Certification> getAllCertifications();
    Certification getCertificationByName(String certificationName);
}
