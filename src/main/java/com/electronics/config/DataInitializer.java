package com.electronics.config;

import com.electronics.service.ProductIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ProductIngestionService ingestionService;

    @Bean
    public ApplicationRunner indexProductsOnStartup() {
        return args -> {
            log.info("Triggering product vector indexing on startup...");
            ingestionService.indexAll();
        };
    }
}
