package com.electronics.service;

import com.electronics.dto.chat.ProductCard;
import com.electronics.entity.Category;
import com.electronics.entity.Product;
import com.electronics.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRetrievalService {

    private final VectorStore vectorStore;
    private final ProductRepository productRepository;

    @Value("${app.chat.max-rag-results:5}")
    private int maxRagResults;

    @Value("${app.chat.max-product-results:6}")
    private int maxProductResults;

    public List<ProductCard> findRelevantProducts(String query) {
        // 1. Vector similarity search
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(maxRagResults)
                .similarityThreshold(0.65)
                .build()
        );

        if (docs.isEmpty()) {
            log.info("No vector results for query: {}", query);
            return List.of();
        }

        // 2. Extract Integer product IDs from document metadata
        List<Integer> productIds = docs.stream()
            .map(doc -> {
                Object raw = doc.getMetadata().get("productId");
                if (raw == null) return null;
                // Metadata values are stored as String in PgVector
                return Integer.parseInt(raw.toString());
            })
            .filter(Objects::nonNull)
            .distinct()
            .limit(maxProductResults)
            .toList();

        if (productIds.isEmpty()) {
            return List.of();
        }

        // 3. Fetch from DB — categories and merchant are JOIN FETCHed,
        //    so no lazy-loading issues when mapping to card
        List<Product> products = productRepository.findActiveByIdIn(productIds);

        // 4. Preserve the vector search ranking order
        Map<Integer, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));

        return productIds.stream()
            .map(productMap::get)
            .filter(Objects::nonNull)
            .map(this::toCard)
            .toList();
    }

    public String buildProductContext(List<ProductCard> products) {
        if (products.isEmpty()) return "";

        StringBuilder sb = new StringBuilder("Relevant products found in catalog:\n\n");
        for (int i = 0; i < products.size(); i++) {
            ProductCard p = products.get(i);
            sb.append(i + 1).append(". ")
                .append(p.getName())
                .append(" — $").append(p.getPrice())
                .append(" | SKU: ").append(p.getSku() != null ? p.getSku() : "N/A")
                .append(" | Categories: ").append(String.join(", ", p.getCategories()))
                .append(" | Merchant: ").append(p.getMerchantName())
                .append(" | ").append(p.isInStock()
                    ? "In Stock (" + p.getStockQuantity() + " units)"
                    : "Out of Stock")
                .append("\n   ").append(p.getDescription())
                .append("\n\n");
        }
        return sb.toString();
    }

    private ProductCard toCard(Product p) {
        return ProductCard.builder()
            .id(p.getId())
            .name(p.getName())
            .description(p.getDescription())
            .price(p.getPrice())
            .imageUrl(p.getImageUrl())
            .sku(p.getSku())
            .categories(
                p.getCategories().stream()
                    .map(Category::getName)
                    .collect(Collectors.toSet())
            )
            .merchantName(p.getMerchant().getEmail())
            .inStock(p.getStockQuantity() > 0)
            .stockQuantity(p.getStockQuantity())
            .actionUrl("/products/" + p.getId())
            .additionalInfo(p.getAdditionalInfo())
            .build();
    }
}
