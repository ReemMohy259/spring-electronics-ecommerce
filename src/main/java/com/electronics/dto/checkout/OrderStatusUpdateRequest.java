package com.electronics.dto.checkout;

import com.electronics.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
    @NotNull(message = "status is required") OrderStatus status) {
}
