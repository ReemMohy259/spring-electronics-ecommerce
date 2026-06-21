package com.electronics.dto;

import jakarta.validation.constraints.NotNull;

public record AddWishlistRequest(

        @NotNull(message = "Product id is required") Integer productId) {
}