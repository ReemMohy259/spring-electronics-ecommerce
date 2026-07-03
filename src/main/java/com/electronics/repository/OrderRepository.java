package com.electronics.repository;

import com.electronics.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN o.orderItems oi
            JOIN oi.product p
            WHERE p.merchant.keycloakId = :keycloakId
            """)
    Page<Order> findByMerchantKeycloakId(
            @Param("keycloakId") String keycloakId,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(oi.currentPrice * oi.quantity), 0)
            FROM OrderItem oi
            WHERE oi.product.merchant.keycloakId = :keycloakId
            """)
    BigDecimal sumRevenueByMerchant(
            @Param("keycloakId") String keycloakId
    );

    @Query("""
            SELECT COUNT(DISTINCT o)
            FROM Order o
            JOIN o.orderItems oi
            JOIN oi.product p
            WHERE p.merchant.keycloakId = :keycloakId
            """)
    long countByMerchantKeycloakId(
            @Param("keycloakId") String keycloakId
    );
}