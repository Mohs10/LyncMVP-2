package com.example.Lync.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CertificationDTO {
    private String certificationName;  // The name of the certification
    private Boolean isCertified;  // The certification status (true/false)

    private MultipartFile certificationFile;
}

