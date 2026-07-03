package com.electronics.repository;

import com.electronics.entity.Payment;
import com.electronics.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Optional<Payment> findByCustomerIdAndStatus(Integer customerId, PaymentStatus status);
}
