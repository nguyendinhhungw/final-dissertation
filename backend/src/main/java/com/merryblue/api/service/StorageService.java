package com.merryblue.api.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(String bucketName, String path, MultipartFile file);
    void deleteFile(String bucketName, String path);
    String getPublicUrl(String bucketName, String path);
}
