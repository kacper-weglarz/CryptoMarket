package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.DTO.response.WalletResponse;
import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.entity.WalletItem;
import io.github.kacperweglarz.cryptomarket.exception.InsufficientFundsException;
import io.github.kacperweglarz.cryptomarket.exception.InvalidAmountException;
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
import java.util.List;
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
    void shouldCreateNewWallet_With_USDTAsset() {
        User user = new User();
        Asset mockAsset = new Asset();
        mockAsset.setAssetSymbol("USDT");

        when(assetService.getOrCreateAsset(anyString(), anyString())).thenReturn(mockAsset);
        when(walletItemRepository.findByWalletIdAndSymbol(any(), anyString())).thenReturn(Optional.empty());

        Wallet wallet = walletService.createWallet(user);

        assertNotNull(wallet);
        assertEquals(user, wallet.getUser());
        verify(walletItemRepository).save(any(WalletItem.class));
    }

    @Test
    void shouldGetUserWallet_WhenExists() {
        Long userId = 1L;
        Long walletId = 1L;

        Asset asset = new Asset();
        asset.setAssetSymbol("BTC");
        asset.setAssetName("Bitcoin");

        WalletItem item = new WalletItem();
        item.setAsset(asset);
        item.setAmount(new BigDecimal("1.5"));
        item.setAvailableBalance(new BigDecimal("1.0"));
        item.setLockedBalance(new BigDecimal("0.5"));

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setWalletItems(List.of(item));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getUserWallet(userId);

        assertNotNull(response);
        assertEquals(walletId, response.getId());
        assertEquals(1, response.getItems().size());
        assertEquals("BTC", response.getItems().get(0).getSymbol());
    }

    @Test
    void shouldDeposit_UpdateExistingUSDT_ViaSQL() {
        Long userId = 1L;
        Long walletId = 10L;
        BigDecimal amount = new BigDecimal("100.00");

        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.depositFunds(walletId, "USDT", amount)).thenReturn(1);

        walletService.deposit(userId, amount);

        verify(walletItemRepository).depositFunds(walletId, "USDT", amount);
        verify(walletItemRepository, never()).save(any());
    }

    @Test
    void shouldDeposit_CreateNewUSDT_WhenUpdateReturnsZero() {
        Long userId = 1L;
        Long walletId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        Asset asset = new Asset();
        asset.setAssetSymbol("USDT");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.depositFunds(walletId, "USDT", amount)).thenReturn(0);
        when(assetService.getOrCreateAsset("USDT", "Tether")).thenReturn(asset);
        when(walletItemRepository.findByWalletIdAndSymbol(walletId, "USDT")).thenReturn(Optional.empty());

        walletService.deposit(userId, amount);

        verify(walletItemRepository).save(any(WalletItem.class));
    }

    @Test
    void shouldThrowException_WhenDepositNegativeAmount() {
        assertThrows(InvalidAmountException.class, () -> walletService.deposit(1L, new BigDecimal("-10")));
        verifyNoInteractions(walletItemRepository);
    }


    @Test
    void shouldTrade_Success_ExistingReceiveAsset() {
        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        Asset assetSpend = new Asset(); assetSpend.setAssetSymbol("USDT");
        Asset assetReceive = new Asset(); assetReceive.setAssetSymbol("BTC");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.subtractFunds(walletId, "USDT", new BigDecimal("50"))).thenReturn(1);
        when(walletItemRepository.depositFunds(walletId, "BTC", new BigDecimal("0.1"))).thenReturn(1);

        walletService.trade(userId, assetSpend, assetReceive, new BigDecimal("50"), new BigDecimal("0.1"));

        verify(walletItemRepository).subtractFunds(walletId, "USDT", new BigDecimal("50"));
        verify(walletItemRepository).depositFunds(walletId, "BTC", new BigDecimal("0.1"));
        verify(walletItemRepository, never()).save(any());
    }

    @Test
    void shouldTrade_Success_CreateNewReceiveAsset() {
        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        Asset assetSpend = new Asset(); assetSpend.setAssetSymbol("USDT");
        Asset assetReceive = new Asset(); assetReceive.setAssetSymbol("DOGE");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.subtractFunds(walletId, "USDT", new BigDecimal("10"))).thenReturn(1);
        when(walletItemRepository.depositFunds(walletId, "DOGE", new BigDecimal("500"))).thenReturn(0);
        when(walletItemRepository.findByWalletIdAndSymbol(walletId, "DOGE")).thenReturn(Optional.empty());

        walletService.trade(userId, assetSpend, assetReceive, new BigDecimal("10"), new BigDecimal("500"));

        verify(walletItemRepository).subtractFunds(walletId, "USDT", new BigDecimal("10"));
        verify(walletItemRepository).save(any(WalletItem.class));
    }

    @Test
    void shouldThrowException_WhenInsufficientFunds_OnTrade() {
        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet(); wallet.setId(walletId);

        Asset assetSpend = new Asset(); assetSpend.setAssetSymbol("USDT");
        Asset assetReceive = new Asset(); assetReceive.setAssetSymbol("BTC");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.subtractFunds(walletId, "USDT", new BigDecimal("1000"))).thenReturn(0);

        assertThrows(InsufficientFundsException.class, () ->
                walletService.trade(userId, assetSpend, assetReceive, new BigDecimal("1000"), new BigDecimal("1"))
        );
        verify(walletItemRepository, never()).depositFunds(anyLong(), anyString(), any());
    }

    @Test
    void shouldLockFunds_Success() {
        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet(); wallet.setId(walletId);
        Asset asset = new Asset(); asset.setAssetSymbol("USDT");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.lockFunds(walletId, "USDT", new BigDecimal("50"))).thenReturn(1);

        walletService.lockFunds(userId, asset, new BigDecimal("50"));

        verify(walletItemRepository).lockFunds(walletId, "USDT", new BigDecimal("50"));
    }

    @Test
    void shouldThrowException_WhenLockFunds_Insufficient() {
        Long userId = 1L;
        Long walletId = 10L;
        Wallet wallet = new Wallet(); wallet.setId(walletId);
        Asset asset = new Asset(); asset.setAssetSymbol("USDT");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletItemRepository.lockFunds(walletId, "USDT", new BigDecimal("50"))).thenReturn(0);

        assertThrows(InsufficientFundsException.class, () ->
                walletService.lockFunds(userId, asset, new BigDecimal("50"))
        );
    }
}