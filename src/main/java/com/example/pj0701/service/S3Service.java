package com.example.pj0701.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${s3.prefix}")
    private String URL_PREFIX;
    private final AmazonS3 amazonS3;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public List<Map<String, Object>> upload(List<MultipartFile> multipartFileList){
        List<Map<String,Object>> fileInfoList= new ArrayList<>();
    log.info("S3Service");

        multipartFileList.forEach(multipartFile -> {
            if(!Objects.requireNonNull(multipartFile.getContentType()).startsWith("image")){
                return;
            }

            String originName = multipartFile.getOriginalFilename();
            String fileName = createFileName(originName);
            long fileSize = multipartFile.getSize();
            ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(fileSize);
                objectMetadata.setContentType(multipartFile.getContentType());

            try(InputStream inputStream = multipartFile.getInputStream()){
                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }catch(IOException e){
                log.error("s3업로드실패");
            }
            String url = URL_PREFIX + fileName;

            //articleNo, originName, fileName, urlPath, fileSize

            Map<String, Object> fileInfoMap= new HashMap<>();
                fileInfoMap.put("originName", originName);
                fileInfoMap.put("uploadName", fileName);
                fileInfoMap.put("size", fileSize);
                fileInfoMap.put("urlPath", url);
            fileInfoList.add(fileInfoMap);
        });
        return fileInfoList;
    }
    //----------------------------------------------날짜_랜덤-랜덤-랜덤-랜덤_오리진.확장자
    public static String createFileName(String originName){

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String current_date = now.format(dateTimeFormatter);
        UUID uuid = UUID.randomUUID();
        return current_date + "_" +
                uuid + "_" +
                originName;
    }
}
