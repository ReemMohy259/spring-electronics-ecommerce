package com.electronics.service;

import com.electronics.dto.checkout.PaymentResponse;
import com.electronics.entity.*;
import com.electronics.exception.payment.PaymentNotFoundException;
import com.electronics.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getMyPayments(Pageable pageable) {
        Customer customer = getCurrentCustomer();
        return paymentRepository.findByCustomerId(customer.getId(), pageable)
            .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getMyPaymentById(Integer paymentId) {
        Customer customer = getCurrentCustomer();
        Payment payment = paymentRepository.findByIdAndCustomerId(paymentId, customer.getId())
            .orElseThrow(() -> new PaymentNotFoundException(
                "Payment not found: " + paymentId));
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByIdAdmin(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(
                "Payment not found: " + paymentId));
        return toResponse(payment);
    }

    private Customer getCurrentCustomer() {
        User user = currentUserService.getCurrentUserEntity();
        if (!(user instanceof Customer customer)) {
            throw new RuntimeException("Only customers can access this resource");
        }
        return customer;
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrder() != null ? payment.getOrder().getId() : null,
            payment.getAmount(),
            payment.getCurrency(),
            payment.getStatus(),
            payment.getCreatedAt(),
            payment.getUpdatedAt())
        ;
    }
}
