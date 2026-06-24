package com.electronics.repository;

import com.electronics.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Integer> {
    Optional<Merchant> findByEmail(String email);
}
