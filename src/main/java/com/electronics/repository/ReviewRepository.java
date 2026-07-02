package com.electronics.repository;

import com.electronics.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findAllByProduct_Id(Integer productId, Pageable pageable);

    Optional<Review> findByUser_EmailAndProduct_Id(String email, Integer productId);

    @Query("""
            select avg(r.rating)
            from Review r
            where r.product.merchant.id = :merchantId
                and r.product.deleted = false
        """)
    Double findAverageRatingByMerchantId(@Param("merchantId") Integer merchantId);
}
