package com.electronics.repository;

import com.electronics.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, Integer> {
    long countByDeletedFalse();

    List<Merchant> findAllByDeletedFalseOrderByCreatedAtDesc();

    Optional<Merchant> findByEmail(String email);

    Optional<Merchant> findByIdAndDeletedFalse(Integer merchantId);
}
