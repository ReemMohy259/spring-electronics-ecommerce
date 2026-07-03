package com.electronics.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageService {

    @Value("${app.storage.root}")
    private String rootDir;

    private Path rootPath;

    private final String PRODUCTS_IMAGE_SUB_DIR = "products";

    private final String PROFILE_IMAGE_SUB_DIR = "profile";

    @PostConstruct
    public void init() {
        this.rootPath = Paths.get(rootDir).toAbsolutePath().normalize();
        log.info("Local storage root: {}", rootPath);
    }

    public Path loadProductImage(String filename) {
        return rootPath.resolve(PRODUCTS_IMAGE_SUB_DIR).resolve(filename).normalize();
    }

    public Path loadProfileImage(String filename) {
        return rootPath.resolve(PROFILE_IMAGE_SUB_DIR).resolve(filename).normalize();
    }

    public String saveProfileImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;

        Path destination = rootPath.resolve(PROFILE_IMAGE_SUB_DIR).resolve(filename).normalize();
        try {
            Files.createDirectories(destination.getParent());
            file.transferTo(destination.toFile());
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to save profile image to " + destination + ": " + e.getMessage(),
                e);
        }

        return filename;
    }

    public void deleteProfileImage(String filename) {
        try {
            Path file = rootPath.resolve(PROFILE_IMAGE_SUB_DIR).resolve(filename).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Failed to delete old profile image: {}", e.getMessage());
        }
    }
}
