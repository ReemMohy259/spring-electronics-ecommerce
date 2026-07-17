package com.electronics.repository;

import com.electronics.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByCustomerId(Integer customerId);

    @Query("""
            SELECT DISTINCT c
            FROM Cart c
            LEFT JOIN FETCH c.cartItems ci
            LEFT JOIN FETCH ci.product
            WHERE c.customer.id = :customerId
        """)
    Optional<Cart> findByCustomerIdWithItems(@Param("customerId") Integer customerId);
}
