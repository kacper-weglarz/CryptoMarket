package io.github.kacperweglarz.cryptomarket.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super("Wallet not initialized " +  message);
    }
}
