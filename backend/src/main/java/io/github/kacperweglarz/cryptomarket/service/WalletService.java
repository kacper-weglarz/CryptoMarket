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
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final AssetService assetService;
    private final WalletItemRepository walletItemRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository, AssetService assetService, WalletItemRepository walletItemRepository) {
        this.walletRepository = walletRepository;
        this.assetService = assetService;
        this.walletItemRepository = walletItemRepository;
    }


    public Wallet createWallet(User user) {

        Asset asset = assetService.getOrCreateAsset("USDT", "Tether");

        Wallet newWallet = new Wallet();
        newWallet.setUser(user);

        if(newWallet.getWalletItems() == null) {
            newWallet.setWalletItems(new ArrayList<>());
        }

        createNewWalletItem(newWallet, asset, BigDecimal.ZERO);

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

        int rowUpdate = walletItemRepository.depositFunds(wallet.getId(), "USDT", amount);

        if (rowUpdate == 0) {
            Asset usdtAsset = assetService.getOrCreateAsset("USDT", "Tether");
            createNewWalletItem(wallet, usdtAsset, amount);
        }
    }

    @Transactional
    public void trade(Long id, Asset assetToSpend, Asset assetToReceive, BigDecimal amountToSpend, BigDecimal amountToReceive) {
        Wallet wallet = getWalletOrThrow(id);

        int subtractResult = walletItemRepository.subtractFunds(wallet.getId(), assetToSpend.getAssetSymbol(), amountToSpend);

        if (subtractResult == 0) {
            throw new InsufficientFundsException(assetToSpend.getAssetSymbol() + " " + amountToSpend);
        }
        int addResult = walletItemRepository.depositFunds(wallet.getId(), assetToReceive.getAssetSymbol(), amountToReceive);

        if (addResult == 0) {
            createNewWalletItem(wallet, assetToReceive, amountToReceive);
        }
    }

    @Transactional
    public void lockFunds(Long id, Asset asset, BigDecimal amountToLock) {
        Wallet wallet = getWalletOrThrow(id);

        int rowsUpdated = walletItemRepository.lockFunds(wallet.getId(), asset.getAssetSymbol(), amountToLock);

        if (rowsUpdated == 0) {
            throw new InsufficientFundsException(asset.getAssetSymbol() + " " + amountToLock);
        }
    }

    private void createNewWalletItem(Wallet wallet, Asset asset, BigDecimal initialAmount) {

        Optional<WalletItem> existing = walletItemRepository.findByWalletIdAndSymbol(wallet.getId(), asset.getAssetSymbol());

        if (existing.isPresent()) {
            walletItemRepository.depositFunds(wallet.getId(), asset.getAssetSymbol(), initialAmount);
        } else {
            WalletItem newItem = new WalletItem();
            newItem.setWallet(wallet);
            newItem.setAsset(asset);
            newItem.setAmount(initialAmount);
            newItem.setAvailableBalance(initialAmount);
            newItem.setLockedBalance(BigDecimal.ZERO);

            walletItemRepository.save(newItem);
        }
    }


    private Wallet getWalletOrThrow(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(""));
    }
}
