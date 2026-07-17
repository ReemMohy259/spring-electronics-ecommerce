package com.electronics.service;

public record ProductIndexEvent(Integer productId, Action action) {

    public enum Action {
        INDEX, REMOVE
    }
}
