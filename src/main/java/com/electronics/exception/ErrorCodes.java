package com.electronics.exception;

public final class ErrorCodes {

    private ErrorCodes() {
    }

    // Cart
    public static final String CART_EMPTY = "CART_EMPTY";

    // Product
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";

    // Category
    public static final String CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";

    // Payment
    public static final String PAYMENT_NOT_FOUND = "PAYMENT_NOT_FOUND";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String PAYMENT_ALREADY_PROCESSED = "PAYMENT_ALREADY_PROCESSED";

    // Checkout
    public static final String ACTIVE_CHECKOUT_EXISTS = "ACTIVE_CHECKOUT_EXISTS";
}
