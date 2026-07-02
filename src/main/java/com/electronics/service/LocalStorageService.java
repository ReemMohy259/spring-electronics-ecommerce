package com.electronics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalStorageService {

    @Value("${app.storage.root}")
    private String rootDir;

    private final String PRODUCTS_IMAGE_SUB_DIR = "products";

    public Path loadProductImage(String filename) {
        return Paths.get(rootDir).resolve(PRODUCTS_IMAGE_SUB_DIR).resolve(filename).normalize();
    }
}
