package io.github.kacperweglarz.cryptomarket.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super("Amount must be greater than or equal to zero " + message);
    }
}
