package io.github.kacperweglarz.cryptomarket.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super("Order not found: " + message);
    }
}
