package io.github.kacperweglarz.cryptomarket.unit.service;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.entity.WalletItem;
import io.github.kacperweglarz.cryptomarket.repository.WalletItemRepository;
import io.github.kacperweglarz.cryptomarket.repository.WalletRepository;
import io.github.kacperweglarz.cryptomarket.service.AssetService;
import io.github.kacperweglarz.cryptomarket.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    AssetService assetService;

    @Mock
    WalletRepository walletRepository;

    @Mock
    WalletItemRepository walletItemRepository;

    @InjectMocks
    WalletService walletService;


    @Test
    void shouldCreateNewWallet() {

        User user = new User();
        Asset usdtAsset = new Asset(); usdtAsset.setAssetSymbol("USDT");
        when(assetService.getOrCreateAsset("USDT", "Tether")).thenReturn(usdtAsset);

        Wallet wallet = walletService.createWallet(user);

        assertNotNull(wallet);

        verify(walletRepository).save(any(Wallet.class));
        verify(walletItemRepository).save(argThat(item ->
                item.getAsset().getAssetSymbol().equals("USDT") &&
                        item.getAvailableBalance().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void shouldDepositToExistingAsset() {
        Long userId = 1L;
        Wallet wallet = new Wallet(); wallet.setId(10L);
        BigDecimal amount = new BigDecimal("100");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.depositFunds(10L, "BTC", amount)).thenReturn(1);

        walletService.deposit(userId, "BTC", amount);

        verify(walletItemRepository).depositFunds(10L, "BTC", amount);
        verify(walletItemRepository, never()).save(any());
    }

    @Test
    void shouldCreateNewItemWhenDepositToNonExistentAsset() {

        Long userId = 1L;
        Wallet wallet = new Wallet(); wallet.setId(10L);
        Asset ethAsset = new Asset(); ethAsset.setAssetSymbol("ETH");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.depositFunds(10L, "ETH", BigDecimal.ONE)).thenReturn(0);
        when(assetService.getOrCreateAsset(eq("ETH"), anyString())).thenReturn(ethAsset);

        walletService.deposit(userId, "ETH", BigDecimal.ONE);

        verify(walletItemRepository).save(any(WalletItem.class));
    }

    @Test
    void shouldLockFundsSuccessfully() {

        Long userId = 1L;
        Wallet wallet = new Wallet(); wallet.setId(10L);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.lockFunds(10L, "USDT", BigDecimal.TEN)).thenReturn(1);

        walletService.lockFunds(userId, "USDT", BigDecimal.TEN);

        verify(walletItemRepository).lockFunds(10L, "USDT", BigDecimal.TEN);
    }

    @Test
    void shouldInitializeWalletWithInitialCapital() {

        Long userId = 1L;
        Wallet wallet = new Wallet(); wallet.setId(10L); wallet.setInitialized(false);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.depositFunds(eq(10L), eq("USDT"), any())).thenReturn(1);

        walletService.initializeWallet(userId);

        assertTrue(wallet.isInitialized());
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldWithdrawLockedFundsSuccessfully() {

        Long userId = 1L;
        Wallet wallet = new Wallet(); wallet.setId(10L);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.decreaseLockedBalance(10L, "BTC", BigDecimal.ONE)).thenReturn(1);

        walletService.withdrawLockedFunds(userId, "BTC", BigDecimal.ONE);

        verify(walletItemRepository).decreaseLockedBalance(10L, "BTC", BigDecimal.ONE);
    }
}