package com.electronics.exception.payment;

import com.electronics.exception.EcommerceException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Map;

public class CartTotalMismatchException extends EcommerceException {

    public CartTotalMismatchException(BigDecimal chargedAmount, BigDecimal currentCartTotal) {
        super(
            "Cart total mismatch. Please refresh and try again.",
            HttpStatus.CONFLICT.value(),
            "CART_TOTAL_MISMATCH",
            Map.of(
                "chargedAmount", chargedAmount,
                "currentCartTotal", currentCartTotal
            )
        );
    }
}
