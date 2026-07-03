package com.electronics.controller;

import com.electronics.exception.FileNotFoundException;
import com.electronics.service.LocalStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final LocalStorageService localStorageService;

    @GetMapping("/products/{filename}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String filename)
        throws IOException {

        Path image = localStorageService.loadProductImage(filename);

        if (!Files.exists(image)) {
            throw new FileNotFoundException(filename);
        }

        Resource resource = new UrlResource(image.toUri());

        String contentType = Files.probeContentType(image);

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }

    @GetMapping("/profiles/{filename}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename)
        throws IOException {

        Path image = localStorageService.loadProfileImage(filename);

        if (!Files.exists(image)) {
            throw new FileNotFoundException(filename);
        }

        Resource resource = new UrlResource(image.toUri());

        String contentType = Files.probeContentType(image);

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }
}
