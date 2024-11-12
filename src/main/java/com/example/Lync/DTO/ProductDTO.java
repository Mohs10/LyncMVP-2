package com.example.Lync.DTO;

import com.example.Lync.Entity.Form;
import com.example.Lync.Entity.Variety;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
public class ProductDTO {
    private Long productId;
    private String productName;
    private String hsnCode;
    private Long categoryId;
    private List<Long> varietyIds;
    private List<Long> formIds;

    private List<Variety> varietys;
    private List<Form> forms;
    private String productImageUrl;
    private String productDescription;
    private MultipartFile productImage;

    private List<CertificationDTO> certifications;
    private List<SpecificationDTO> specifications;
    private boolean activeProduct ;

}



