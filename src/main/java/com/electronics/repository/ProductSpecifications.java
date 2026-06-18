package com.electronics.repository;

import com.electronics.entity.Category;
import com.electronics.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> isActive() {
        return (root, query, builder) -> builder.isFalse(root.get("deleted"));
    }

    public static Specification<Product> hasCategory(Integer categoryId) {
        return (root, query, builder) -> {
            Join<Product, Category> categories = root.join("categories", JoinType.INNER);
            query.distinct(true);
            return builder.equal(categories.get("id"), categoryId);
        };
    }

    public static Specification<Product> priceAtLeast(BigDecimal minimumPrice) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"),
                minimumPrice);
    }

    public static Specification<Product> priceAtMost(BigDecimal maximumPrice) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), maximumPrice);
    }

    public static Specification<Product> containsKeyword(String keyword) {
        String searchTerm = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("name")), searchTerm),
                builder.like(builder.lower(root.get("description").as(String.class)), searchTerm),
                builder.like(builder.lower(root.get("sku")), searchTerm));
    }
}
