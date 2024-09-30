package com.example.Lync.Config;


import com.example.Lync.Entity.Product;
import com.example.Lync.Repository.ProductRepository;
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

@Service
public class S3Service {


    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final ProductRepository productRepository;


    public S3Service(S3Client s3Client, S3Presigner s3Presigner, ProductRepository productRepository) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.productRepository = productRepository;
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
    public String getUserImagePresignedUrl(String userId, String fileName) {
        String s3Key = "users/" + userId + "/images/" + fileName;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
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
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }



    // Upload two images for the product
    public Map<String, String> uploadProductImages(Long productId, MultipartFile image1, MultipartFile image2) throws IOException {
        // Define the S3 keys for product images
        String fileName1 = image1.getOriginalFilename();
        String fileName2 = image2.getOriginalFilename();

        String s3KeyImage1 = "products/" + productId + "/images/image1_" + fileName1;
        String s3KeyImage2 = "products/" + productId + "/images/image2_" + fileName2;

        // Upload the first product image
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3KeyImage1)
                        .build(),
                RequestBody.fromBytes(image1.getBytes()));

        // Upload the second product image
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3KeyImage2)
                        .build(),
                RequestBody.fromBytes(image2.getBytes()));

        // Return the S3 keys of the uploaded images
        Map<String, String> uploadedImageKeys = new HashMap<>();
        uploadedImageKeys.put("image1", s3KeyImage1);
        uploadedImageKeys.put("image2", s3KeyImage2);

        return uploadedImageKeys;  // Return the S3 keys for both images
    }

    // Generate presigned URLs to retrieve both product images
    public Map<String, String> getProductImagesPresignedUrls(Long productId) {
        Product product =  productRepository.findById(productId).get();

        String s3KeyImage1 = product.getProductImageUrl1() ;
        String s3KeyImage2 = product.getProductImageUrl2();

        // Generate the presigned URL for the first product image
        GetObjectRequest getObjectRequest1 = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3KeyImage1)
                .build();

        GetObjectPresignRequest presignRequest1 = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest1)
                .build();

        String presignedUrl1 = s3Presigner.presignGetObject(presignRequest1).url().toString();

        // Generate the presigned URL for the second product image
        GetObjectRequest getObjectRequest2 = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3KeyImage2)
                .build();

        GetObjectPresignRequest presignRequest2 = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest2)
                .build();

        String presignedUrl2 = s3Presigner.presignGetObject(presignRequest2).url().toString();

        // Return the presigned URLs for both images
        Map<String, String> presignedUrls = new HashMap<>();
        presignedUrls.put("image1", presignedUrl1);
        presignedUrls.put("image2", presignedUrl2);

        return presignedUrls;  // Return presigned URLs for both images
    }
}

