package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SellerProductImageDto {

    private String spId;
    private Long productId;
    private String sellerId;
    private MultipartFile productImageUrl1;
    private MultipartFile productImageUrl2;

    private MultipartFile productCertificationUrl;

    private MultipartFile npopCertification;
    private MultipartFile nopCertification;
    private MultipartFile euCertification;
    private MultipartFile gsdcCertification;
    private MultipartFile ipmCertification;
}
