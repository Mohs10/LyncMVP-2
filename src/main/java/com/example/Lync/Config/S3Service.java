package com.example.Lync.Config;


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

@Service
public class S3Service {


    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;


    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
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
}

