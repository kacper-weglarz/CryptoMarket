package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.DTO.request.SpotOrderRequest;
import io.github.kacperweglarz.cryptomarket.DTO.response.SpotOrderResponse;
import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.Order;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderSide;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderStatus;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderType;
import io.github.kacperweglarz.cryptomarket.exception.InvalidAmountException;
import io.github.kacperweglarz.cryptomarket.exception.PriceNotFoundException;
import io.github.kacperweglarz.cryptomarket.repository.OrderRepository;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import io.github.kacperweglarz.cryptomarket.service.MarketDataService;
import io.github.kacperweglarz.cryptomarket.service.OrderService;
import io.github.kacperweglarz.cryptomarket.service.TradingPairService;
import io.github.kacperweglarz.cryptomarket.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    WalletService walletService;

    @Mock
    UserRepository userRepository;

    @Mock
    TradingPairService tradingPairService;

    @Mock
    MarketDataService marketDataService;

    @InjectMocks
    OrderService orderService;

    private User user;
    private TradingPair pair;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        Asset btcAsset = new Asset();
        btcAsset.setAssetSymbol("BTC");

        Asset usdtAsset = new Asset();
        usdtAsset.setAssetSymbol("USDT");

        pair = new TradingPair();
        pair.setTradingPairSymbol("BTC/USDT");
        pair.setBaseAsset(btcAsset);
        pair.setQuoteAsset(usdtAsset);
    }

    @Test
    void shouldExecuteMarketBuyOrderSuccessfully() {

        SpotOrderRequest request = new SpotOrderRequest();

        request.setSymbol("BTC/USDT");
        request.setAmount(new BigDecimal("0.1"));
        request.setOrderSide(OrderSide.BUY);
        request.setOrderType(OrderType.MARKET);

        BigDecimal currentPrice = new BigDecimal("50000");
        BigDecimal expectedCost = new BigDecimal("5000.00");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(pair);
        when(marketDataService.getCurrentPrice("BTC/USDT")).thenReturn(currentPrice);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        SpotOrderResponse response = orderService.placeSpotOrder(1L, request);

        assertEquals(OrderStatus.FILLED, response.getStatus());

        verify(walletService).lockFunds(1L, "USDT", expectedCost);
        verify(walletService).withdrawLockedFunds(1L, "USDT", expectedCost);
        verify(walletService).deposit(
                eq(1L),
                eq("BTC"),
                argThat(val -> val != null && val.compareTo(new BigDecimal("0.1")) == 0));
    }

    @Test
    void shouldExecuteLimitBuyOrderAndLockFunds() {

        SpotOrderRequest request = new SpotOrderRequest();

        request.setSymbol("BTC/USDT");
        request.setAmount(new BigDecimal("1"));
        request.setPrice(new BigDecimal("40000"));
        request.setOrderSide(OrderSide.BUY);
        request.setOrderType(OrderType.LIMIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(pair);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        SpotOrderResponse response = orderService.placeSpotOrder(1L, request);

        assertEquals(OrderStatus.PENDING, response.getStatus());

        verify(walletService).lockFunds(1L, "USDT", new BigDecimal("40000.00"));
        verify(walletService, never()).withdrawLockedFunds(any(), any(), any());
    }


    @Test
    void shouldCancelOrderAndUnlockFunds() {

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setTradingPair(pair);
        order.setSide(OrderSide.BUY);
        order.setLockedAmount(new BigDecimal("5000"));
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByIdWithLock(100L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L, 100L);

        assertEquals(OrderStatus.CANCELED, order.getStatus());

        verify(walletService).unlockFunds(1L, "USDT", new BigDecimal("5000"));
        verify(orderRepository).save(order);
    }


    @Test
    void shouldThrowExceptionForInvalidAmount() {

        SpotOrderRequest request = new SpotOrderRequest();
        request.setAmount(new BigDecimal("-1.5"));

        assertThrows(InvalidAmountException.class, () -> orderService.placeSpotOrder(1L, request));
    }
}