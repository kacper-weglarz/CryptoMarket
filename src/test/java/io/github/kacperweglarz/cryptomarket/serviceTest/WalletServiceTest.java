package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.DTO.response.WalletResponse;
import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.entity.WalletItem;
import io.github.kacperweglarz.cryptomarket.exception.InvalidAmountException;
import io.github.kacperweglarz.cryptomarket.exception.WalletAssetNotFoundException;
import io.github.kacperweglarz.cryptomarket.exception.WalletNotFoundException;
import io.github.kacperweglarz.cryptomarket.repository.WalletRepository;
import io.github.kacperweglarz.cryptomarket.service.AssetService;
import io.github.kacperweglarz.cryptomarket.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private AssetService assetService;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldCreateNewWallet_With_USDTAsset() {
        User user = new User();
        Asset mockAsset = new Asset();
        mockAsset.setAssetSymbol("USDT");

        when(assetService.getOrCreateAsset(anyString(), anyString())).thenReturn(mockAsset);

        Wallet wallet = walletService.createWallet(user);

        assertNotNull(wallet);
        assertEquals(user, wallet.getUser());
        assertNotNull(wallet.getWalletItems());
        assertFalse(wallet.getWalletItems().isEmpty());
        assertEquals(1, wallet.getWalletItems().size());
        assertEquals("USDT", wallet.getWalletItems().get(0).getAsset().getAssetSymbol());
        assertEquals(BigDecimal.ZERO, wallet.getWalletItems().get(0).getAvailableBalance());
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
        wallet.setWalletItems(new ArrayList<>());
        wallet.getWalletItems().add(item);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getUserWallet(userId);

        assertNotNull(response);
        assertEquals(walletId, response.getId());
        assertEquals(1, response.getItems().size());

        WalletResponse.WalletItemResponse itemDto = response.getItems().get(0);

        assertEquals("BTC", itemDto.getSymbol());
        assertEquals("Bitcoin", itemDto.getName());
        assertEquals(new BigDecimal("1.5"), itemDto.getAmount());
        assertEquals(new BigDecimal("1.0"), itemDto.getAvailable());
        assertEquals(new BigDecimal("0.5"), itemDto.getLocked());

        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    void shouldReturnEmptyList_WhenWalletHasNoItems() {
        Long userId = 1L;

        Wallet emptyWallet = new Wallet();
        emptyWallet.setId(1L);
        emptyWallet.setWalletItems(new ArrayList<>());

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(emptyWallet));

        WalletResponse response = walletService.getUserWallet(userId);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void shouldThrowException_WhenWalletNotFound() {
        Long userId = 1L;

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getUserWallet(userId);
        });
    }

    @Test
    void shouldDepositMoney_Successfully() {
        Long userId = 1L;
        BigDecimal depositAmount = new BigDecimal("100.00");

        Wallet wallet = new Wallet();
        wallet.setWalletItems(new ArrayList<>());

        Asset usdtAsset = new Asset();
        usdtAsset.setAssetSymbol("USDT");

        WalletItem usdtItem = new WalletItem();
        usdtItem.setAsset(usdtAsset);
        usdtItem.setAmount(BigDecimal.ZERO);
        usdtItem.setAvailableBalance(BigDecimal.ZERO);
        wallet.getWalletItems().add(usdtItem);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        walletService.deposit(userId, depositAmount);

        assertEquals(new BigDecimal("100.00"), usdtItem.getAmount());
        assertEquals(new BigDecimal("100.00"), usdtItem.getAvailableBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void shouldThrowException_WhenDepositAmountIsNegative() {
        Long userId = 1L;
        BigDecimal negativeAmount = new BigDecimal("-50.00");

        assertThrows(InvalidAmountException.class, () -> {
            walletService.deposit(userId, negativeAmount);
        });

        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_WhenUSDT_AssetNotFoundInWallet() {
        Long userId = 1L;
        Wallet wallet = new Wallet();
        wallet.setWalletItems(new ArrayList<>());

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertThrows(WalletAssetNotFoundException.class, () -> {
            walletService.deposit(userId, new BigDecimal("10.00"));
        });
    }
}