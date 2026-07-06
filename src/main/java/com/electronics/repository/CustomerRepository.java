package com.electronics.repository;

import com.electronics.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    long countByDeletedFalse();

    List<Customer> findAllByDeletedFalseOrderByCreatedAtDesc();

    Optional<Customer> findByIdAndDeletedFalse(Integer customerId);
}
