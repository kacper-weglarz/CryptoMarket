package io.github.kacperweglarz.cryptomarket.exception;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(String message) {
        super("Price not found: "  + message);
    }
}
