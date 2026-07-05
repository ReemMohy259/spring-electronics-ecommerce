package com.electronics.service;

import com.electronics.entity.Category;
import com.electronics.entity.Product;
import com.electronics.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIngestionService {

    private final VectorStore vectorStore;
    private final ProductRepository productRepository;

    /**
     * Call this whenever a product is created or updated.
     * Builds the embedding text from the entity fields and upserts into the vector store.
     */
    @Transactional(readOnly = true)
    public void indexProduct(Product product) {
        if (Boolean.TRUE.equals(product.getDeleted())) {
            // If the product was soft-deleted, remove it from the vector store too
            removeFromIndex(product.getId());
            return;
        }

        String categoryNames = product.getCategories().stream()
            .map(Category::getName)
            .collect(Collectors.joining(", "));

        // Rich text for embedding — the more descriptive, the better the search quality
        String embeddingText = """
            Product: %s
            SKU: %s
            Categories: %s
            Merchant: %s
            Description: %s
            Price: $%s
            Additional info: %s
            """.formatted(
            product.getName(),
            product.getSku() != null ? product.getSku() : "",
            categoryNames,
            product.getMerchant().getEmail(),
            product.getDescription() != null ? product.getDescription() : "",
            product.getPrice(),
            product.getAdditionalInfo() != null ? product.getAdditionalInfo() : ""
        );

        Document doc = new Document(
            embeddingText,
            Map.of(
                "productId", product.getId().toString(),  // always store as String in metadata
                "name",      product.getName(),
                "categories", categoryNames
            )
        );

        vectorStore.add(List.of(doc));
        log.info("Indexed product: {} (id={})", product.getName(), product.getId());
    }

    /**
     * Bulk index — call this for initial data load or re-indexing.
     */
    @Transactional(readOnly = true)
    public void indexAll() {
        List<Product> products = productRepository.findAllActive();
        log.info("Starting bulk indexing of {} products", products.size());
        products.forEach(this::indexProduct);
        log.info("Bulk indexing complete");
    }

    /**
     * Remove a product from the vector store (on hard delete or when toggling deleted=true).
     * The document ID in PgVector is derived from the productId metadata — we use a
     * filter-based delete here since we stored productId in metadata.
     */
    public void removeFromIndex(Integer productId) {
        // PgVectorStore's delete(List<String>) expects the internal document UUIDs,
        // not our product IDs. Use filter-based search + delete instead.
        var docsToDelete = vectorStore.similaritySearch(
            org.springframework.ai.vectorstore.SearchRequest.builder()
                .query("*")
                .topK(1)
                .filterExpression("productId == '" + productId + "'")
                .build()
        );
        if (!docsToDelete.isEmpty()) {
            vectorStore.delete(docsToDelete.stream().map(Document::getId).toList());
            log.info("Removed product {} from vector index", productId);
        }
    }
}
