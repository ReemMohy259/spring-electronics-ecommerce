package com.electronics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class IntentClassifier {

    private final ChatClient.Builder chatClientBuilder;

    private static final List<Pattern> PRODUCT_PATTERNS = List.of(
        Pattern.compile("\\b(show|find|recommend|suggest|search|look for|get)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(product|item|buy|purchase|price|cost|cheap|expensive|budget)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(best|top|good|latest|available|in stock)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(laptop|phone|camera|headphone|tv|tablet|watch)\\b", Pattern.CASE_INSENSITIVE)
        // Add your own domain-specific keywords here
    );

    private static final String CLASSIFIER_PROMPT = """
        You are a binary intent classifier. Your only job is to decide if the user's message
        is asking about products, shopping, recommendations, prices, or anything commerce-related.
        
        Respond with ONLY the word "true" or "false". Nothing else. No explanation.
        
        Examples:
        "What laptops do you have under $500?" -> true
        "I need a gift for my dad" -> true
        "Compare these two phones" -> true
        "What is your return policy?" -> false
        "Hello, how are you?" -> false
        "What are your store hours?" -> false
        """;

    public boolean isProductIntent(String message) {
        String result = chatClientBuilder.build()
            .prompt()
            .system(CLASSIFIER_PROMPT)
            .user(message)
            .call()
            .content();

        if (result == null) {
            return fallbackKeywordMatch(message);
        }

        return "true".equalsIgnoreCase(result.trim());
    }

    private boolean fallbackKeywordMatch(String message) {
        return PRODUCT_PATTERNS.stream()
            .anyMatch(pattern -> pattern.matcher(message).find());
    }
}
