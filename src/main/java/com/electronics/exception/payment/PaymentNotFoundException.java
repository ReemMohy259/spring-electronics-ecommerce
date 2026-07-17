package com.electronics.exception.payment;

import com.electronics.exception.EcommerceException;
import com.electronics.exception.ErrorCodes;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class PaymentNotFoundException extends EcommerceException {

    public PaymentNotFoundException(String paymentIntentId) {
        this("Payment not found for intent: " + paymentIntentId, paymentIntentId);
    }

    public PaymentNotFoundException(String message, String paymentIntentId) {
        super(message, HttpStatus.NOT_FOUND.value(), ErrorCodes.PAYMENT_NOT_FOUND,
            Map.of("paymentIntentId", paymentIntentId));
    }
}
