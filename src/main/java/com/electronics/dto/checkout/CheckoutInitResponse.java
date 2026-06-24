package com.electronics.dto.checkout;

import java.math.BigDecimal;

public record CheckoutInitResponse(String clientSecret, String paymentIntentId, String currency,
    BigDecimal amount) {
}
