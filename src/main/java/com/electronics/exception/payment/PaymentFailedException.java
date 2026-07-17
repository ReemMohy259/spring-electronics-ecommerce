package com.electronics.exception.payment;

import com.electronics.exception.EcommerceException;
import com.electronics.exception.ErrorCodes;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentFailedException extends EcommerceException {

    public PaymentFailedException(String paymentIntentId, String reason) {
        this("Payment failed" + (paymentIntentId != null ? " for intent " + paymentIntentId : "")
            + ": " + reason, paymentIntentId, reason);
    }

    public PaymentFailedException(String message, String paymentIntentId, String reason) {
        super(message, HttpStatus.PAYMENT_REQUIRED.value(), ErrorCodes.PAYMENT_FAILED,
            buildInfo(paymentIntentId, reason));
    }

    private static Map<String, Object> buildInfo(String paymentIntentId, String reason) {
        Map<String, Object> info = new LinkedHashMap<>();
        if (paymentIntentId != null) {
            info.put("paymentIntentId", paymentIntentId);
        }
        info.put("reason", reason);
        return info;
    }
}