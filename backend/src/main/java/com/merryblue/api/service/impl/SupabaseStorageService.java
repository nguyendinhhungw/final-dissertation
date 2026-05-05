package com.merryblue.api.service.impl;

import com.merryblue.api.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupabaseStorageService implements StorageService {

    private final WebClient webClient;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Override
    public String uploadFile(String bucketName, String path, MultipartFile file) {
        log.info("Uploading file to bucket: {}, path: {}", bucketName, path);
        // Logic to upload to Supabase Storage using WebClient
        // For brevity, this is a skeleton implementation
        return getPublicUrl(bucketName, path);
    }

    @Override
    public void deleteFile(String bucketName, String path) {
        log.info("Deleting file from bucket: {}, path: {}", bucketName, path);
    }

    @Override
    public String getPublicUrl(String bucketName, String path) {
        return String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, path);
    }
}
