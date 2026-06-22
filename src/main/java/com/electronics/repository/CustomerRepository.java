package com.electronics.repository;

import com.electronics.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByIdAndDeletedFalse(Integer customerId);
}
