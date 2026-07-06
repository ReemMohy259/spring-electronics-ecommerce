package com.electronics.exception.payment;

import com.electronics.entity.PaymentStatus;
import com.electronics.exception.EcommerceException;
import com.electronics.exception.ErrorCodes;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class PaymentAlreadyProcessedException extends EcommerceException {

    public PaymentAlreadyProcessedException(String paymentIntentId, PaymentStatus status) {
        this("Payment " + paymentIntentId + " has already been processed (status: " + status + ")",
            paymentIntentId, status);
    }

    public PaymentAlreadyProcessedException(
        String message,
        String paymentIntentId,
        PaymentStatus status) {
        super(message, HttpStatus.CONFLICT.value(), ErrorCodes.PAYMENT_ALREADY_PROCESSED,
            Map.of("paymentIntentId", paymentIntentId, "status", status.name()));
    }
}
