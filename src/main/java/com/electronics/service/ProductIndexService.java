package com.electronics.service;

import com.electronics.document.ProductDocument;
import com.electronics.entity.Product;
import com.electronics.repository.ProductRepository;
import com.electronics.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIndexService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public void ensureIndexExists() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(ProductDocument.class);
        if (!indexOps.exists()) {
            indexOps.create();
            Document mapping = indexOps.createMapping();
            indexOps.putMapping(mapping);
            log.info("Created Elasticsearch index 'products'");
        }
    }

    @Transactional(readOnly = true)
    public void indexProduct(Integer productId) {
        productRepository.findByIdAndDeletedFalse(productId)
            .ifPresentOrElse(
                this::indexProduct,
                () -> log.warn("Product {} not found or deleted, skipping index", productId));
    }

    @Transactional(readOnly = true)
    public void indexProduct(Product product) {
        try {
            ProductDocument doc = mapToDocument(product);
            elasticsearchOperations.save(doc);
            log.debug("Indexed product {} in Elasticsearch", product.getId());
        } catch (Exception e) {
            log.error("Failed to index product {}: {}", product.getId(), e.getMessage());
        }
    }

    public void removeProduct(Integer productId) {
        try {
            elasticsearchOperations.delete(String.valueOf(productId), ProductDocument.class);
            log.debug("Removed product {} from Elasticsearch", productId);
        } catch (Exception e) {
            log.error(
                "Failed to remove product {} from Elasticsearch: {}",
                productId,
                e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public void reindexAll() {
        ensureIndexExists();
        List<Product> products = productRepository.findAll()
            .stream()
            .filter(p -> !Boolean.TRUE.equals(p.getDeleted()))
            .toList();
        if (products.isEmpty()) {
            log.info("No active products to index");
            return;
        }
        List<ProductDocument> documents = products.stream().map(this::mapToDocument).toList();
        elasticsearchOperations.save(documents);
        log.info("Reindexed {} products into Elasticsearch", documents.size());
    }

    private ProductDocument mapToDocument(Product product) {
        Double avgRating = reviewRepository.findAverageRatingByProductId(product.getId());
        if (avgRating == null)
            avgRating = 0.0;

        List<String> categoryNames = product.getCategories()
            .stream()
            .map(c -> c.getName())
            .toList();

        return ProductDocument.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .rating(avgRating)
            .categories(categoryNames)
            .createdAt(product.getCreatedAt())
            .imageUrl(product.getImageUrl())
            .sku(product.getSku())
            .stockQuantity(product.getStockQuantity())
            .build();
    }
}
