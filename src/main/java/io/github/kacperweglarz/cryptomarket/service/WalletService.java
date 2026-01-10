package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.DTO.response.WalletResponse;
import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.entity.WalletItem;
import io.github.kacperweglarz.cryptomarket.exception.InsufficientFundsException;
import io.github.kacperweglarz.cryptomarket.exception.InvalidAmountException;
import io.github.kacperweglarz.cryptomarket.exception.WalletAssetNotFoundException;
import io.github.kacperweglarz.cryptomarket.exception.WalletNotFoundException;
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

    @Autowired
    public WalletService(WalletRepository walletRepository, AssetService assetService) {
        this.walletRepository = walletRepository;
        this.assetService = assetService;
    }


    public Wallet createWallet(User user) {

        Asset asset = assetService.getOrCreateAsset("USDT", "Tether");

        Wallet newWallet = new Wallet();
        newWallet.setUser(user);

        if(newWallet.getWalletItems() == null) {
            newWallet.setWalletItems(new ArrayList<>());
        }

        createNewWalletItem(newWallet, asset);

        return newWallet;
    }

    public WalletResponse getUserWallet(Long id) {

        Wallet wallet = getWalletOrThrow(id);

        List<WalletResponse.WalletItemResponse> itemsDto = wallet.getWalletItems().stream()
                .map(item -> new WalletResponse.WalletItemResponse(
                        item.getAsset().getAssetSymbol(),
                        item.getAsset().getAssetName(),
                        item.getAmount(),
                        item.getAvailableBalance(),
                        item.getLockedBalance()
                ))
                .toList();

        return new WalletResponse(wallet.getId(), itemsDto);
    }

    @Transactional
    public void deposit(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException(amount.toString());
        }

        Wallet wallet = getWalletOrThrow(id);
        Asset usdtAsset = assetService.getOrCreateAsset("USDT", "Tether");

        addFunds(wallet, usdtAsset, amount);

        walletRepository.save(wallet);
    }

    @Transactional
    public void trade(Long id, Asset assetToSpend, Asset assetToReceive, BigDecimal amountToSpend, BigDecimal amountToReceive) {
        Wallet wallet = getWalletOrThrow(id);

        subtractFunds(wallet, assetToSpend, amountToSpend);
        addFunds(wallet, assetToReceive, amountToReceive);

        walletRepository.save(wallet);
    }

    @Transactional
    public void lockFunds(Long id, Asset asset, BigDecimal amountToLock) {
        Wallet wallet = getWalletOrThrow(id);

        WalletItem item = wallet.getWalletItems().stream()
                .filter(i -> i.getAsset().getAssetSymbol().equals(asset.getAssetSymbol()))
                .findFirst()
                .orElseThrow(() -> new WalletAssetNotFoundException(asset.getAssetSymbol()));

        if (item.getAvailableBalance().compareTo(amountToLock) < 0) {
            throw new InsufficientFundsException(amountToLock.toString() + " available balance: " +  item.getAvailableBalance());
        }

        item.setAvailableBalance(item.getAvailableBalance().subtract(amountToLock));
        item.setLockedBalance(item.getLockedBalance().add(amountToLock));

        walletRepository.save(wallet);
    }

    private void addFunds(Wallet wallet, Asset asset, BigDecimal amount) {
        WalletItem item = wallet.getWalletItems().stream()
                .filter(i -> i.getAsset().getAssetSymbol().equals(asset.getAssetSymbol()))
                .findFirst()
                .orElseGet(() -> createNewWalletItem(wallet, asset));

        item.setAmount(item.getAmount().add(amount));
        item.setAvailableBalance(item.getAvailableBalance().add(amount));
    }

    private void subtractFunds(Wallet wallet, Asset asset, BigDecimal amount) {
        WalletItem item = wallet.getWalletItems().stream()
                .filter(i -> i.getAsset().getAssetSymbol().equals(asset.getAssetSymbol()))
                .findFirst()
                .orElseThrow(() -> new WalletAssetNotFoundException(asset.getAssetSymbol()));

        if (item.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(amount + " available balance: " +  item.getAvailableBalance());
        }

        item.setAmount(item.getAmount().subtract(amount));
        item.setAvailableBalance(item.getAvailableBalance().subtract(amount));
    }

    private WalletItem createNewWalletItem(Wallet wallet, Asset asset) {
        WalletItem newItem = new WalletItem();
        newItem.setWallet(wallet);
        newItem.setAsset(asset);
        newItem.setAmount(BigDecimal.ZERO);
        newItem.setAvailableBalance(BigDecimal.ZERO);
        newItem.setLockedBalance(BigDecimal.ZERO);

        wallet.getWalletItems().add(newItem);
        return newItem;
    }

    private Wallet getWalletOrThrow(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not initialized"));
    }
}
