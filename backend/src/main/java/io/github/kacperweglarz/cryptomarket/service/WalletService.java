package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.DTO.response.WalletResponse;
import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.entity.WalletItem;
import io.github.kacperweglarz.cryptomarket.exception.InsufficientFundsException;
import io.github.kacperweglarz.cryptomarket.exception.InvalidAmountException;
import io.github.kacperweglarz.cryptomarket.exception.WalletNotFoundException;
import io.github.kacperweglarz.cryptomarket.repository.WalletItemRepository;
import io.github.kacperweglarz.cryptomarket.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final AssetService assetService;
    private final WalletItemRepository walletItemRepository;
    private static final BigDecimal INITIAL_CAPITAL = new BigDecimal("50000.00");

    @Autowired
    public WalletService(WalletRepository walletRepository, AssetService assetService, WalletItemRepository walletItemRepository) {
        this.walletRepository = walletRepository;
        this.assetService = assetService;
        this.walletItemRepository = walletItemRepository;
    }


    public Wallet createWallet(User user) {

        Wallet newWallet = new Wallet();
        newWallet.setUser(user);
        newWallet.setInitialized(false);
        newWallet.setWalletItems(new ArrayList<>());

        walletRepository.save(newWallet);

        Asset ustdAsset = assetService.getOrCreateAsset("USDT", "Tether");
        createNewWalletItem(newWallet, ustdAsset, BigDecimal.ZERO);

        return newWallet;
    }

    @Transactional(readOnly = true)
    public WalletResponse getUserWallet(Long id) {

        Wallet wallet = getWalletOrThrow(id);

        List<WalletResponse.WalletItemResponse> itemsDto = wallet.getWalletItems().stream()
                .filter(item -> item.getTotalBalance().compareTo(BigDecimal.ZERO) > 0)
                .map(item -> new WalletResponse.WalletItemResponse(
                        item.getAsset().getAssetSymbol(),
                        item.getAsset().getAssetName(),
                        item.getTotalBalance(),
                        item.getAvailableBalance(),
                        item.getLockedBalance()
                ))
                .toList();

        return new WalletResponse(wallet.getId(), wallet.isInitialized(), itemsDto);
    }

    @Transactional
    public void initializeWallet(Long userId) {

        Wallet wallet = getWalletOrThrow(userId);

        if (wallet.isInitialized()) {
            throw new InsufficientFundsException("Wallet has already been initialized! Cannot deposit initial funds again.");
        }

        int updatedRows = walletItemRepository.depositFunds(wallet.getId(), "USDT", INITIAL_CAPITAL);

        if (updatedRows == 0) {
            Asset usdtAsset = assetService.getOrCreateAsset("USDT", "Tether");
            createNewWalletItem(wallet, usdtAsset, INITIAL_CAPITAL);
        }

        wallet.setInitialized(true);
        walletRepository.save(wallet);
    }

    @Transactional
    public void deposit(Long id, String symbol, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Amount must be greater than or equal to zero: " + amount);
        }

        Wallet wallet = getWalletOrThrow(id);
        String upperSymbol = symbol.toUpperCase();

        int rowUpdate = walletItemRepository.depositFunds(wallet.getId(), upperSymbol, amount);

        if (rowUpdate == 0) {
            Asset usdtAsset = assetService.getOrCreateAsset(upperSymbol, "Unknown Asset");
            createNewWalletItem(wallet, usdtAsset, amount);
        }
    }

    @Transactional
    public void lockFunds(Long id, String symbol, BigDecimal amountToLock) {
        Wallet wallet = getWalletOrThrow(id);

        int rowsUpdated = walletItemRepository.lockFunds(wallet.getId(), symbol, amountToLock);

        if (rowsUpdated == 0) {
            throw new InsufficientFundsException(symbol + "Lock amount " + amountToLock);
        }
    }

    @Transactional
    public void unlockFunds(Long id, String symbol, BigDecimal amountToUnlock) {
        Wallet wallet = getWalletOrThrow(id);

        int rowsUpdated = walletItemRepository.unlockFunds(wallet.getId(), symbol, amountToUnlock);

        if (rowsUpdated == 0) {
            throw new InsufficientFundsException(symbol + "Unlock amount " + amountToUnlock);
        }
    }

    @Transactional
    public void withdrawLockedFunds(Long userId, String symbol, BigDecimal amount) {

        Wallet wallet = getWalletOrThrow(userId);

        int rows = walletItemRepository.decreaseLockedBalance(wallet.getId(), symbol, amount);

        if (rows == 0) {
            throw new InsufficientFundsException("Locked funds missing " + symbol + " : " + amount);
        }
    }

    @Transactional
    public void transferFundsAfterTrade(Long userId, String symbol, BigDecimal amount) {
        deposit(userId, symbol, amount);
    }

    private void createNewWalletItem(Wallet wallet, Asset asset, BigDecimal initialAvailable) {
        WalletItem newItem = new WalletItem();

        newItem.setWallet(wallet);
        newItem.setAsset(asset);
        newItem.setAvailableBalance(initialAvailable);
        newItem.setLockedBalance(BigDecimal.ZERO);

        walletItemRepository.save(newItem);
    }


    private Wallet getWalletOrThrow(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(""));
    }
}
