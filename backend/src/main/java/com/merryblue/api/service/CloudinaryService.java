package com.merryblue.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class CloudinaryService {

    public String uploadImage(MultipartFile file) {
        log.info("Mock uploading image to Cloudinary: {}", file.getOriginalFilename());
        return "https://res.cloudinary.com/merryblue/image/upload/v12345/mock-image.png";
    }

    public void deleteImage(String publicId) {
        log.info("Mock deleting image from Cloudinary: {}", publicId);
    }
}
