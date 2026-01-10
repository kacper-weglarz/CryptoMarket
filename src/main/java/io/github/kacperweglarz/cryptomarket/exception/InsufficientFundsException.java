package io.github.kacperweglarz.cryptomarket.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super("Insufficient funds: " + message);
    }
}
