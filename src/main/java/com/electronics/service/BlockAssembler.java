package com.electronics.service;

import com.electronics.dto.chat.ContentBlock;
import com.electronics.dto.chat.ProductCard;
import com.electronics.dto.chat.ProductsBlock;
import com.electronics.dto.chat.TextBlock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parses the raw LLM JSON output and assembles final ContentBlock list.
 * The LLM decides ordering and text; we inject real product data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BlockAssembler {

    private final ObjectMapper objectMapper;

    /**
     * @param llmRawOutput  The JSON string the LLM returned
     * @param products      Hydrated products from the DB (ordered by relevance)
     */
    public List<ContentBlock> assemble(String llmRawOutput, List<ProductCard> products) {
        List<ContentBlock> blocks = new ArrayList<>();

        try {
            // Strip markdown code fences if the model wrapped them
            String json = stripCodeFences(llmRawOutput);

            List<Map<String, Object>> rawBlocks = objectMapper.readValue(
                json, new TypeReference<>() {}
            );

            for (Map<String, Object> raw : rawBlocks) {
                String type = (String) raw.get("type");

                switch (type) {
                    case "text" -> {
                        String text = (String) raw.get("text");
                        if (text != null && !text.isBlank()) {
                            blocks.add(new TextBlock(text));
                        }
                    }
                    case "products" -> {
                        // The LLM signals "show products here" — we supply the real data
                        String label = (String) raw.getOrDefault("label", "Recommended products");
                        if (!products.isEmpty()) {
                            blocks.add(new ProductsBlock(products, label));
                        }
                    }
                    default -> log.warn("Unknown block type from LLM: {}", type);
                }
            }

        } catch (Exception e) {
            // If the LLM returned malformed JSON, fall back to treating it as plain text
            log.error("Failed to parse LLM output as JSON, falling back to text block", e);
            blocks.add(new TextBlock(llmRawOutput));
        }

        return blocks;
    }

    private String stripCodeFences(String raw) {
        return raw.replaceAll("(?s)```json\\s*", "")
            .replaceAll("(?s)```\\s*", "")
            .trim();
    }
}
