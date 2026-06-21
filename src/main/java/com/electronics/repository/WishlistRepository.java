package com.electronics.repository;

import com.electronics.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    Page<Wishlist> findAllByUser_Email(String email, Pageable pageable);

    Optional<Wishlist> findByUser_EmailAndProduct_Id(String email, Integer productId);
}
