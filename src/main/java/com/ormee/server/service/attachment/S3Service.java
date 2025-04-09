package com.ormee.server.service.attachment;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service() {
        this.amazonS3 = AmazonS3ClientBuilder.standard().build();
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        File file = convertMultipartFileToFile(multipartFile);
        try {
            String fileName = LocalDate.now() + multipartFile.getOriginalFilename();

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file));

            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (AmazonServiceException e) {
            throw new CustomException(ExceptionType.S3_REQUEST_FAILED_EXCEPTION);
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }

    public void deleteFile(String fileName) {
        try {
            if(amazonS3.doesObjectExist(bucketName, fileName)) {
                amazonS3.deleteObject(bucketName, fileName);
            } else {
                throw new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION);
            }
        } catch (AmazonServiceException e) {
            throw new CustomException(ExceptionType.S3_REQUEST_FAILED_EXCEPTION);
        }
    }
}
