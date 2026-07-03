package com.electronics.repository;

import com.electronics.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

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

    Page<Product> findByMerchantKeycloakIdAndDeletedFalse(
        String merchantKeycloakId,
        Pageable pageable);

    Page<Product> findByMerchantKeycloakIdAndDeletedFalseOrderBySoldUnitsDesc(
        String merchantKeycloakId,
        Pageable pageable);

    long countByMerchantKeycloakIdAndDeletedFalse(String merchantKeycloakId);

    List<Product> findTop20ByDeletedFalseAndStockQuantityGreaterThanOrderBySoldUnitsDescCreatedAtDesc(
        Integer stockQuantity);

    @Query("""
            SELECT p FROM Product p
            WHERE p.deleted = false
            ORDER BY (SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.product.id = p.id) DESC
        """)
    Page<Product> findFeatured(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findAllByIdForUpdate(List<Integer> ids);
}
