package com.electronics.repository;

import com.electronics.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
    extends
        JpaRepository<Product, Integer>,
        JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"merchant", "categories"})
    Optional<Product> findByIdAndDeletedFalse(Integer id);

    Optional<Product> findByIdAndDeletedFalseAndMerchantKeycloakId(Integer id, String merchant_keycloakId);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Integer id);

    boolean existsByCategoriesId(Integer categoryId);

    List<Product> findTop20ByDeletedFalseAndStockQuantityGreaterThanOrderBySoldUnitsDescCreatedAtDesc(
        Integer stockQuantity);
}
