package com.electronics.exception.checkout;

import com.electronics.dto.checkout.CheckoutInitResponse;
import com.electronics.exception.EcommerceException;
import com.electronics.exception.ErrorCodes;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class ActiveCheckoutExistsException extends EcommerceException {

    public ActiveCheckoutExistsException(CheckoutInitResponse existingCheckout) {
        super(
            "An active checkout already exists. Resume it, or cancel it before starting a new one.",
            HttpStatus.CONFLICT.value(), ErrorCodes.ACTIVE_CHECKOUT_EXISTS,
            Map.of("checkout", existingCheckout));
    }
}
