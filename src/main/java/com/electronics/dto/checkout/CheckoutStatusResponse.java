package com.electronics.dto.checkout;

public record CheckoutStatusResponse(Status status, OrderResponse order) {

    public enum Status {
        PENDING, PAID, FAILED
    }

    public static CheckoutStatusResponse pending() {
        return new CheckoutStatusResponse(Status.PENDING, null);
    }

    public static CheckoutStatusResponse paid(OrderResponse order) {
        return new CheckoutStatusResponse(Status.PAID, order);
    }

    public static CheckoutStatusResponse failed() {
        return new CheckoutStatusResponse(Status.FAILED, null);
    }
}
