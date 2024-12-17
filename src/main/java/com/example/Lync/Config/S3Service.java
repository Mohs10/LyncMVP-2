package com.example.Lync.Config;


import com.example.Lync.DTO.SellerProductImageDto;
import com.example.Lync.Entity.Product;
import com.example.Lync.Entity.SellerProduct;
import com.example.Lync.Repository.ProductRepository;
import com.example.Lync.Repository.SellerProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class S3Service {


    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final ProductRepository productRepository;
    private final SellerProductRepository sellerProductRepository;


    public S3Service(S3Client s3Client, S3Presigner s3Presigner, ProductRepository productRepository, SellerProductRepository sellerProductRepository) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.productRepository = productRepository;
        this.sellerProductRepository = sellerProductRepository;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));
        return fileName;
    }


    public String uploadUserImage(String userId, MultipartFile file) throws IOException {
        // Define the S3 key for user image as users/{userId}/images/{fileName}
        String fileName = file.getOriginalFilename();
        String s3Key = "users/" + userId + "/images/" + fileName;

        // Upload the image to the specific S3 key
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        return s3Key;  // Return the S3 key of the uploaded image
    }


    public String uploadUserCertificate(String userId, MultipartFile file) throws IOException {
        // Define the S3 key for user license as users/{userId}/license/{fileName}
        String fileName = file.getOriginalFilename();
        String s3Key = "users/" + userId + "/certificate/" + fileName;

        // Upload the license to the specific S3 key
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        return s3Key;  // Return the S3 key of the uploaded license
    }

    public String uploadUserCancelledCheque(String userId, MultipartFile file) throws IOException {
        // Define the S3 key for user license as users/{userId}/license/{fileName}
        String fileName = file.getOriginalFilename();
        String s3Key = "users/" + userId + "/cancelledCheque/" + fileName;

        // Upload the license to the specific S3 key
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        return s3Key;  // Return the S3 key of the uploaded license
    }

    public String uploadUserLicense(String userId, MultipartFile file) throws IOException {
        // Define the S3 key for user license as users/{userId}/license/{fileName}
        String fileName = file.getOriginalFilename();
        String s3Key = "users/" + userId + "/license/" + fileName;

        // Upload the license to the specific S3 key
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        return s3Key;  // Return the S3 key of the uploaded license
    }


    // Generate a presigned URL to retrieve the user's image
    public String getUserImagePresignedUrl(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }


    public String getUserCertificatePresignedUrl(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }


    public String getUserCancelledChequePresignedUrl(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // Generate a presigned URL to retrieve the user's license
    public String getUserLicensePresignedUrl(String userId, String fileName) {
        String s3Key = "users/" + userId + "/license/" + fileName;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }



    // Upload two images for the product
//    public Map<String, String> uploadProductImages(Long productId, MultipartFile image1, MultipartFile image2) throws IOException {
//        // Define the S3 keys for product images
//        String fileName1 = image1.getOriginalFilename();
//        String fileName2 = image2.getOriginalFilename();
//
//        String s3KeyImage1 = "products/" + productId + "/images/image1_" + fileName1;
//        String s3KeyImage2 = "products/" + productId + "/images/image2_" + fileName2;
//
//        // Upload the first product image
//        s3Client.putObject(PutObjectRequest.builder()
//                        .bucket(bucketName)
//                        .key(s3KeyImage1)
//                        .build(),
//                RequestBody.fromBytes(image1.getBytes()));
//
//        // Upload the second product image
//        s3Client.putObject(PutObjectRequest.builder()
//                        .bucket(bucketName)
//                        .key(s3KeyImage2)
//                        .build(),
//                RequestBody.fromBytes(image2.getBytes()));
//
//        // Return the S3 keys of the uploaded images
//        Map<String, String> uploadedImageKeys = new HashMap<>();
//        uploadedImageKeys.put("image1", s3KeyImage1);
//        uploadedImageKeys.put("image2", s3KeyImage2);
//
//        return uploadedImageKeys;  // Return the S3 keys for both images
//    }


    public String uploadProductImage(Long productId, MultipartFile image) throws IOException {
        // Define the S3 key for the product image
        String fileName = image.getOriginalFilename();
        String s3KeyImage = "products/" + productId + "/images/" + "image_" + fileName;

        // Upload the product image
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3KeyImage)
                        .build(),
                RequestBody.fromBytes(image.getBytes()));

        // Return the S3 key of the uploaded image
        return s3KeyImage;
    }

    public String getProductImagePresignedUrl(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }


    // Generate presigned URLs to retrieve both product images
    public Map<String, String> getProductImagesPresignedUrls(Long productId) {
        Product product =  productRepository.findById(productId).get();

        String s3KeyImage1 = "product.getProductImageUrl1()" ;
        String s3KeyImage2 = "product.getProductImageUrl2()";

        // Generate the presigned URL for the first product image
        GetObjectRequest getObjectRequest1 = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3KeyImage1)
                .build();

        GetObjectPresignRequest presignRequest1 = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest1)
                .build();

        String presignedUrl1 = s3Presigner.presignGetObject(presignRequest1).url().toString();

        // Generate the presigned URL for the second product image
        GetObjectRequest getObjectRequest2 = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3KeyImage2)
                .build();

        GetObjectPresignRequest presignRequest2 = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest2)
                .build();

        String presignedUrl2 = s3Presigner.presignGetObject(presignRequest2).url().toString();

        // Return the presigned URLs for both images
        Map<String, String> presignedUrls = new HashMap<>();
        presignedUrls.put("image1", presignedUrl1);
        presignedUrls.put("image2", presignedUrl2);

        return presignedUrls;  // Return presigned URLs for both images
    }


    public String uploadFileToS3(MultipartFile file, String folder) throws IOException {
        // Generate unique key for the file in the S3 bucket
        String key = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Create metadata map for file content type and length
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        // Create the S3 PutObjectRequest with metadata
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .metadata(metadata)
                .build();

        // Upload the file to S3
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Return the S3 key (file path)
        return key;
    }



//    public String uploadFileToS3(MultipartFile file, String folder) throws IOException {
//        String fileName = file.getOriginalFilename();
//        String key = folder + "/" + UUID.randomUUID() + "-" + fileName;
//
//        s3Client.putObject(PutObjectRequest.builder()
//                        .bucket(bucketName)
//                        .key(key)
//                        .build(),
//                RequestBody.fromBytes(file.getBytes()));
//
//        // Return the S3 key (file path) of the uploaded file
//        return key;
//    }

    public SellerProduct addSellerProductImages(SellerProductImageDto sellerProductImageDto) throws IOException {
        SellerProduct sellerProduct =  sellerProductRepository.findById(sellerProductImageDto.getSpId()).orElseThrow(()->
                new RuntimeException("Seller Product not found for ID: " + sellerProductImageDto.getSpId()));

        if (sellerProductImageDto.getProductImageUrl1() != null && !sellerProductImageDto.getProductImageUrl1().isEmpty()) {
            String productImageUrl1 = uploadFileToS3(sellerProductImageDto.getProductImageUrl1(), "product-images");
            sellerProduct.setProductImageUrl1(productImageUrl1);
        }

        if (sellerProductImageDto.getProductImageUrl2() != null && !sellerProductImageDto.getProductImageUrl2().isEmpty()) {
            String productImageUrl2 = uploadFileToS3(sellerProductImageDto.getProductImageUrl2(), "product-images");
            sellerProduct.setProductImageUrl2(productImageUrl2);
        }
//
//        if (sellerProductImageDto.getProductCertificationUrl() != null && !sellerProductImageDto.getProductCertificationUrl().isEmpty()) {
//            String certificationUrl = uploadFileToS3(sellerProductImageDto.getProductCertificationUrl(), "certifications");
//            sellerProduct.setProductCertificationUrl(certificationUrl);
//        }
//
//        // Upload NPOP certification if exists
//        if (sellerProductImageDto.getNpopCertification() != null && !sellerProductImageDto.getNpopCertification().isEmpty()) {
//            String npopCertificationUrl = uploadFileToS3(sellerProductImageDto.getNpopCertification(), "certifications");
//            sellerProduct.setNpopCertification(npopCertificationUrl);
//        }
//
//        // Upload NOP certification if exists
//        if (sellerProductImageDto.getNopCertification() != null && !sellerProductImageDto.getNopCertification().isEmpty()) {
//            String nopCertificationUrl = uploadFileToS3(sellerProductImageDto.getNopCertification(), "certifications");
//            sellerProduct.setNopCertification(nopCertificationUrl);
//        }
//
//        // Upload EU certification if exists
//        if (sellerProductImageDto.getEuCertification() != null && !sellerProductImageDto.getEuCertification().isEmpty()) {
//            String euCertificationUrl = uploadFileToS3(sellerProductImageDto.getEuCertification(), "certifications");
//            sellerProduct.setEuCertification(euCertificationUrl);
//        }
//
//        // Upload GSDC certification if exists
//        if (sellerProductImageDto.getGsdcCertification() != null && !sellerProductImageDto.getGsdcCertification().isEmpty()) {
//            String gsdcCertificationUrl = uploadFileToS3(sellerProductImageDto.getGsdcCertification(), "certifications");
//            sellerProduct.setGsdcCertification(gsdcCertificationUrl);
//        }
//
//        // Upload IPM certification if exists
//        if (sellerProductImageDto.getIpmCertification() != null && !sellerProductImageDto.getIpmCertification().isEmpty()) {
//            String ipmCertificationUrl = uploadFileToS3(sellerProductImageDto.getIpmCertification(), "certifications");
//            sellerProduct.setIpmCertification(ipmCertificationUrl);
//        }

        return sellerProductRepository.save(sellerProduct);
    }

    public Map<String, String> getProductImageUrls(String spId) throws Exception {
        SellerProduct product = sellerProductRepository.findById(spId).orElse(null);

        Map<String, String> imageUrls = new HashMap<>();

        // Add the image URLs if they exist
        assert product != null;
        if (product.getProductImageUrl1() != null) {
            imageUrls.put("productImageUrl1", product.getProductImageUrl1());
        }
        if (product.getProductImageUrl2() != null) {
            imageUrls.put("productImageUrl2", product.getProductImageUrl2());
        }
//        if (product.getProductCertificationUrl() != null) {
//            imageUrls.put("productCertificationUrl", product.getProductCertificationUrl());
//        }
//
//        // Add certification URLs if they exist
//        if (product.getNpopCertification() != null) {
//            imageUrls.put("npopCertification", product.getNpopCertification());
//        }
//        if (product.getNopCertification() != null) {
//            imageUrls.put("nopCertification", product.getNopCertification());
//        }
//        if (product.getEuCertification() != null) {
//            imageUrls.put("euCertification", product.getEuCertification());
//        }
//        if (product.getGsdcCertification() != null) {
//            imageUrls.put("gsdcCertification", product.getGsdcCertification());
//        }
//        if (product.getIpmCertification() != null) {
//            imageUrls.put("ipmCertification", product.getIpmCertification());
//        }

        return imageUrls;
    }



    public String uploadSellerProductImage1(String userId ,String productId, MultipartFile image) throws IOException {
        // Define the S3 key for the product image
        String fileName = image.getOriginalFilename();
        String s3Key = "users/" + userId + "/Product/"+productId+"/image1/" + fileName;

        // Upload the product image
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(image.getBytes()));

        // Return the S3 key of the uploaded image
        return s3Key;
    }

    public String getSellerProductImage1Url(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
    public String uploadSellerProductImage2(String userId ,String productId, MultipartFile image) throws IOException {
        // Define the S3 key for the product image
        String fileName = image.getOriginalFilename();
        String s3Key = "users/" + userId + "/Product/"+productId+"/image2/" + fileName;

        // Upload the product image
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(image.getBytes()));

        // Return the S3 key of the uploaded image
        return s3Key;
    }
    public String getSellerProductImage2Url(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public String uploadSellerProductCertificate(String userId ,String productId, MultipartFile certificate) throws IOException {
        // Define the S3 key for the product image
        String fileName = certificate.getOriginalFilename();
        String s3Key = "users/" + userId + "/Product/"+productId+"/certificate/" + fileName;

        // Upload the product image
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(certificate.getBytes()));

        // Return the S3 key of the uploaded image
        return s3Key;
    }

    public String getSellerProductCertificateUrl(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }


    public String uploadSellerSOP( MultipartFile certificate) throws IOException {
        String fileName = certificate.getOriginalFilename();
        String s3Key = "SOPs/Seller/"+ fileName;

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(certificate.getBytes()));

        return s3Key;
    }

    public String getSellerSOP(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }


    public String uploadBuyerSOP( MultipartFile certificate) throws IOException {
        String fileName = certificate.getOriginalFilename();
        String s3Key = "SOPs/Buyer/"+ fileName;

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(certificate.getBytes()));

        // Return the S3 key of the uploaded image
        return s3Key;
    }

    public String getBuyerSOP(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public String uploadPurchaseOrder(String orderId, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String s3Key = "Orders/" + orderId +"/PurchaseOrder/" + fileName;

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        // Return the S3 key of the uploaded image
        return s3Key;
    }

    public String getPurchaseOrder(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public String uploadOrderInvoice(String orderId, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String s3Key = "Orders/" + orderId +"/Invoices/" + fileName;

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        // Return the S3 key of the uploaded invoice
        return s3Key;
    }
    public String getOrderInvoice(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }



    public String uploadSampleInvoice(String queryId, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String s3Key = "Query/Sample/" + queryId +"/Invoices/" + fileName;

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        // Return the S3 key of the uploaded invoice
        return s3Key;
    }
    public String getSampleInvoice(String url) {
        String s3Key = url;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }




}

