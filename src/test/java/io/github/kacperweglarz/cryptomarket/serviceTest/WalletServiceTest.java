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
class WalletServiceTest {

    @Mock
    AssetService assetService;

    @Mock
    WalletRepository walletRepository;

    @InjectMocks
    WalletService walletService;

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
    void shouldDeposit_AddFunds_ToExistingUSDT() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        Wallet wallet = new Wallet();
        wallet.setWalletItems(new ArrayList<>());

        Asset usdt = new Asset(); usdt.setAssetSymbol("USDT");

        WalletItem item = new WalletItem(1L, wallet, usdt, new BigDecimal("50.00"), new BigDecimal("50.00"), BigDecimal.ZERO);
        wallet.getWalletItems().add(item);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(assetService.getOrCreateAsset("USDT", "Tether")).thenReturn(usdt);

        walletService.deposit(userId, amount);

        assertEquals(new BigDecimal("150.00"), item.getAmount());
        assertEquals(new BigDecimal("150.00"), item.getAvailableBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldThrowException_WhenDepositAmountIsNegative() {
        Long id = 1L;
        BigDecimal negativeAmount = new BigDecimal("-50.00");

        assertThrows(InvalidAmountException.class, () -> {
            walletService.deposit(id, negativeAmount);
        });

        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldDeposit_CreateUSDT() {
        Long id = 1L;
        Wallet wallet = new Wallet();
        wallet.setWalletItems(new ArrayList<>());

        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");

        when(walletRepository.findByUserId(id)).thenReturn(Optional.of(wallet));
        when(assetService.getOrCreateAsset("USDT", "Tether")).thenReturn(assetUSDT);

        walletService.deposit(id, new BigDecimal("100.00"));

        assertFalse(wallet.getWalletItems().isEmpty());
        assertEquals("USDT", wallet.getWalletItems().get(0).getAsset().getAssetSymbol());
        assertEquals(new BigDecimal("100.00"), wallet.getWalletItems().get(0).getAmount());
    }

    @Test
    void shouldThrowException_WhenDepositNegativeAmount() {
        assertThrows(InvalidAmountException.class,
                () -> walletService.deposit(1L, new BigDecimal("-10.00")));

        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldTrade_Success_ExistingReceiveAsset() {
        Long id = 1L;
        Wallet wallet = new Wallet();
        wallet.setWalletItems(new ArrayList<>());

        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");
        Asset assetBTC = new Asset(); assetBTC.setAssetSymbol("BTC");

        WalletItem usdtItem = new WalletItem(1L, wallet, assetUSDT, new BigDecimal("100"), new BigDecimal("100"), BigDecimal.ZERO);
        WalletItem btcItem = new WalletItem(2L, wallet, assetBTC, new BigDecimal("0.5"), new BigDecimal("0.5"), BigDecimal.ZERO);

        wallet.getWalletItems().add(usdtItem);
        wallet.getWalletItems().add(btcItem);

        when(walletRepository.findByUserId(id)).thenReturn(Optional.of(wallet));

        walletService.trade(id, assetUSDT, assetBTC, new BigDecimal("50"), new BigDecimal("0.1"));

        assertEquals(new BigDecimal("50"), usdtItem.getAmount());
        assertEquals(new BigDecimal("0.6"), btcItem.getAmount());
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldTrade_Success_CreateNewReceiveAsset() {
        Long id = 1L;
        Wallet wallet = new Wallet();
        wallet.setWalletItems(new ArrayList<>());

        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");
        Asset assetDOGE = new Asset(); assetDOGE.setAssetSymbol("DOGE");

        WalletItem usdtItem = new WalletItem(1L, wallet, assetUSDT, new BigDecimal("100"), new BigDecimal("100"), BigDecimal.ZERO);
        wallet.getWalletItems().add(usdtItem);

        when(walletRepository.findByUserId(id)).thenReturn(Optional.of(wallet));

        walletService.trade(id, assetUSDT, assetDOGE, new BigDecimal("10"), new BigDecimal("500"));

        assertEquals(new BigDecimal("90"), usdtItem.getAmount());
        assertEquals(2, wallet.getWalletItems().size());
        WalletItem dogeItem = wallet.getWalletItems().stream()
                .filter(i -> i.getAsset().getAssetSymbol().equals("DOGE"))
                .findFirst().orElseThrow();
        assertEquals(new BigDecimal("500"), dogeItem.getAmount());
    }

    @Test
    void shouldThrowException_WhenInsufficientFunds() {
        Long id = 1L;
        Wallet wallet = new Wallet();
        wallet.setWalletItems(new ArrayList<>());

        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");
        Asset assetBTC = new Asset(); assetBTC.setAssetSymbol("BTC");

        WalletItem usdtItem = new WalletItem(1L, wallet, assetUSDT, new BigDecimal("10"), new BigDecimal("10"), BigDecimal.ZERO);
        wallet.getWalletItems().add(usdtItem);

        when(walletRepository.findByUserId(id)).thenReturn(Optional.of(wallet));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                walletService.trade(id, assetUSDT, assetBTC, new BigDecimal("50"), new BigDecimal("1"))
        );

        assertTrue(ex.getMessage().contains("Insufficient funds"));
        assertEquals(1, wallet.getWalletItems().size());
    }

    @Test
    void shouldThrowException_WhenAssetToSpend_NotFound() {
        Long id = 1L;
        Wallet wallet = new Wallet();
        wallet.setWalletItems(new ArrayList<>());

        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");
        WalletItem usdtItem = new WalletItem(1L, wallet, assetUSDT, new BigDecimal("100"), new BigDecimal("100"), BigDecimal.ZERO);
        wallet.getWalletItems().add(usdtItem);

        Asset eth = new Asset(); eth.setAssetSymbol("ETH");
        Asset btc = new Asset(); btc.setAssetSymbol("BTC");

        when(walletRepository.findByUserId(id)).thenReturn(Optional.of(wallet));

        assertThrows(WalletAssetNotFoundException.class, () ->
                walletService.trade(id, eth, btc, new BigDecimal("10"), new BigDecimal("1"))
        );
    }
}