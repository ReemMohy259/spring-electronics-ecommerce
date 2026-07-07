package com.electronics;

import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {OpenAiEmbeddingAutoConfiguration.class,
        OpenAiEmbeddingAutoConfiguration.class})
public class ElectronicsEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectronicsEcommerceApplication.class, args);
    }

}
