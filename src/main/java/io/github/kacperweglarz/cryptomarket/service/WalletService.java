package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.DTO.response.WalletResponse;
import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.entity.WalletItem;
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

        WalletItem walletItem = new WalletItem();
        walletItem.setWallet(newWallet);
        walletItem.setAsset(asset);
        walletItem.setAmount(BigDecimal.ZERO);
        walletItem.setAvailableBalance(BigDecimal.ZERO);
        walletItem.setLockedBalance(BigDecimal.ZERO);

        newWallet.getWalletItems().add(walletItem);


        return newWallet;
    }

    public WalletResponse getUserWallet(Long id) {

        Wallet wallet = walletRepository.findByUserId(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not initialized"));

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

        Wallet wallet = walletRepository.findByUserId(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not initialized"));

        WalletItem userItemUSDT = wallet.getWalletItems().stream()
                .filter(item -> "USDT".equals(item.getAsset().getAssetSymbol()))
                .findFirst()
                .orElseThrow(() -> new WalletAssetNotFoundException("USTD"));

        userItemUSDT.setAmount(userItemUSDT.getAmount().add(amount));
        userItemUSDT.setAvailableBalance(userItemUSDT.getAvailableBalance().add(amount));

        walletRepository.save(wallet);
    }
}
