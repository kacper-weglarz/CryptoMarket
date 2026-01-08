package io.github.kacperweglarz.cryptomarket.exception;

public class WalletAssetNotFoundException extends RuntimeException {
    public WalletAssetNotFoundException(String message) {
        super("Wallet does not contain asset: " + message);
    }
}
