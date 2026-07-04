package com.electronics.dto.checkout;

import com.electronics.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentResponse(
    Integer id,
    Integer orderId,
    BigDecimal amount,
    String currency,
    PaymentStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
