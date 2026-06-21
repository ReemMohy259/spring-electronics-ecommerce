package com.electronics.repository;

import com.electronics.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findAllByProduct_Id(Integer productId, Pageable pageable);

    Optional<Review> findByUser_EmailAndProduct_Id(String email, Integer productId);
}
