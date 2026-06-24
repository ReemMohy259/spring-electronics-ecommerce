package com.electronics.dto.checkout;

import jakarta.validation.constraints.NotBlank;

public record ConfirmCheckoutRequest(

    @NotBlank(message = "Payment intent ID is required") String paymentIntentId) {
}
