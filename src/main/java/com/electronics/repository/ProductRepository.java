package com.electronics.repository;

import com.electronics.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository
    extends
        JpaRepository<Product, Integer>,
        JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"merchant", "categories"})
    Optional<Product> findByIdAndDeletedFalse(Integer id);

    Optional<Product> findByIdAndDeletedFalseAndMerchantKeycloakId(
        Integer id,
        String merchant_keycloakId);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Integer id);

    boolean existsByCategoriesId(Integer categoryId);

    long countByMerchantIdAndDeletedFalse(Integer merchantId);

    List<Product> findTop20ByDeletedFalseAndStockQuantityGreaterThanOrderBySoldUnitsDescCreatedAtDesc(
        Integer stockQuantity);

    @Query(value = """
        SELECT p FROM Product p
        WHERE p.deleted = false
        AND (:categoryId IS NULL OR :categoryId IN (SELECT c.id FROM p.categories c))
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY (SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.product.id = p.id) DESC
        """, countQuery = """
        SELECT COUNT(p) FROM Product p
        WHERE p.deleted = false
        AND (:categoryId IS NULL OR :categoryId IN (SELECT c.id FROM p.categories c))
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Product> findFeatured(
        @Param("categoryId") Integer categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("keyword") String keyword,
        Pageable pageable);
}
