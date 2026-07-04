package com.electronics.repository;

import com.electronics.entity.Customer;
import com.electronics.entity.Order;
import com.electronics.entity.Payment;
import com.electronics.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Optional<Payment> findByCustomerIdAndStatus(Integer customerId, PaymentStatus status);

    Page<Payment> findByCustomerId(Integer customerId, Pageable pageable);

    Optional<Payment> findByIdAndCustomerId(Integer id, Integer customerId);

    Optional<Payment> findByOrder(Order order);
}
