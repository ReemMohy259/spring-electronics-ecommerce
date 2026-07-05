package com.electronics.service;

import com.electronics.dto.chat.ChatRequest;
import com.electronics.dto.chat.ChatResponse;
import com.electronics.dto.chat.ContentBlock;
import com.electronics.dto.chat.ProductCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient.Builder chatClientBuilder;
    private final ChatMemory chatMemory;
    private final IntentClassifier intentClassifier;
    private final ProductRetrievalService productRetrievalService;
    private final BlockAssembler blockAssembler;

    private static final String SYSTEM_PROMPT = """
        You are a helpful shopping assistant. You help users find products and answer questions.

        CRITICAL INSTRUCTION — OUTPUT FORMAT:
        You MUST always respond with a valid JSON array of content blocks.
        Never respond with plain text or markdown outside of the JSON structure.

        Available block types:

        1. Text block — for all prose, explanations, answers:
           { "type": "text", "text": "Your message here" }

        2. Products block — ONLY when you want to show product cards:
           { "type": "products", "label": "A short label like 'Top picks for you'" }
           DO NOT include product data in this block. The system handles that.
           Only include this block if products were found and are relevant.

        RULES:
        - Always start with a text block introducing your answer.
        - If products are relevant and were provided in context, include a products block AFTER your text.
        - If no products are relevant, do NOT include a products block.
        - Keep text concise and helpful.
        - Never fabricate product names, prices, or specs. Only reference what is in the context.

        EXAMPLE response when products are relevant:
        [
          { "type": "text", "text": "Here are some laptops that match your budget under $1000:" },
          { "type": "products", "label": "Laptops under $1000" }
        ]

        EXAMPLE response for a general question:
        [
          { "type": "text", "text": "Our return policy allows returns within 30 days of purchase." }
        ]
        """;

    public ChatResponse chat(ChatRequest request) {
        String sessionId = request.getSessionId();
        String userMessage = request.getMessage();

        log.info("Chat request — session: {}, message length: {}", sessionId, userMessage.length());

        // 1. Classify intent
        boolean isProductQuery = intentClassifier.isProductIntent(userMessage);
        log.info("Product intent detected: {}", isProductQuery);

        // 2. Retrieve relevant products if needed
        List<ProductCard> products = List.of();
        String productContext = "";

        if (isProductQuery) {
            products = productRetrievalService.findRelevantProducts(userMessage);
            productContext = productRetrievalService.buildProductContext(products);
            log.info(
                "Retrieved {} products from vector store: {}",
                products.size(),
                productContext);
        }

        // 3. Build augmented user message with RAG context
        String augmentedMessage = buildAugmentedMessage(userMessage, productContext);

        String llmOutput = chatClientBuilder.build()
            .prompt()
            .system(SYSTEM_PROMPT)
            .user(augmentedMessage)
            .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .advisors(advisors -> advisors.param(ChatMemory.CONVERSATION_ID, sessionId))
            .call()
            .content();

        // 5. Parse LLM output and assemble typed blocks
        List<ContentBlock> blocks = blockAssembler.assemble(llmOutput, products);

        return ChatResponse.builder()
            .sessionId(sessionId)
            .blocks(blocks)
            .timestamp(Instant.now())
            .build();
    }

    private String buildAugmentedMessage(String userMessage, String productContext) {
        if (productContext.isBlank()) {
            return userMessage;
        }
        return """
            User question: %s

            --- PRODUCT CATALOG CONTEXT (from semantic search) ---
            %s
            --- END CONTEXT ---

            Use the above context to answer the question. Only reference products listed above.
            """.formatted(userMessage, productContext);
    }
}
