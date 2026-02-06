package io.github.kacperweglarz.cryptomarket.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super("Error:  " + message);
    }
}
