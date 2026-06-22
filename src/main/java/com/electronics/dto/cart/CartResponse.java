package com.electronics.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    Integer id,
    Integer totalQuantity,
    BigDecimal totalPrice,
    List<CartItemResponse> items
) {
}
