package com.electronics.exception.cart;

import com.electronics.exception.EcommerceException;
import com.electronics.exception.ErrorCodes;
import org.springframework.http.HttpStatus;

public class EmptyCartException extends EcommerceException {

    public EmptyCartException() {
        super("Your cart is empty.", HttpStatus.BAD_REQUEST.value(), ErrorCodes.CART_EMPTY);
    }

    public EmptyCartException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value(), ErrorCodes.CART_EMPTY);
    }
}
