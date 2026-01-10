package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.DTO.request.SpotOrderRequest;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
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


    @Test
     void shouldPlace_LimitOrder_Successfully() {
        User user = new User();
        user.setId(1L);

        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");
        Asset assetBTC = new Asset(); assetBTC.setAssetSymbol("BTC");

        TradingPair tradingPair = new TradingPair();
        tradingPair.setBaseAsset(assetBTC);
        tradingPair.setQuoteAsset(assetUSDT);

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setAmount(new BigDecimal("0.5"));
        request.setOrderType(OrderType.LIMIT);
        request.setOrderSide(OrderSide.BUY);
        request.setPrice(new BigDecimal("40000"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(tradingPair);

        orderService.placeOrder(1L, request);

        verify(walletService, never()).trade(any(), any(), any(), any(), any());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertNotNull(savedOrder);
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertEquals(new BigDecimal("40000"), savedOrder.getPrice());
        assertEquals(new BigDecimal("0.5"), savedOrder.getAmount());
        assertEquals(OrderType.LIMIT, savedOrder.getType());
        assertEquals(user, savedOrder.getUser());
    }

    @Test
    void shouldPlace_MarketBuyOrder_Successfully() {
        User user = new User(); user.setId(1L);
        Asset assetBTC = new Asset(); assetBTC.setAssetSymbol("BTC");
        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");

        TradingPair tradingPair = new TradingPair();
        tradingPair.setBaseAsset(assetBTC);
        tradingPair.setQuoteAsset(assetUSDT);

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.MARKET);
        request.setOrderSide(OrderSide.BUY);
        request.setAmount(new BigDecimal("1000"));

        BigDecimal marketPrice = new BigDecimal("50000");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(tradingPair);
        when(marketDataService.getCurrentPrice("BTC/USDT")).thenReturn(marketPrice);

        orderService.placeOrder(1L, request);

        verify(walletService).trade(
                eq(1L),
                eq(assetUSDT),
                eq(assetBTC),
                eq(new BigDecimal("1000")),
                eq(new BigDecimal("0.02000000"))
        );

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        assertEquals(OrderStatus.FILLED, orderCaptor.getValue().getStatus());
        assertEquals(marketPrice, orderCaptor.getValue().getPrice());
    }

    @Test
    void shouldPlace_MarketSellOrder_Successfully() {

        User user = new User(); user.setId(1L);
        Asset assetBTC = new Asset(); assetBTC.setAssetSymbol("BTC");
        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");

        TradingPair tradingPair = new TradingPair();
        tradingPair.setBaseAsset(assetBTC);
        tradingPair.setQuoteAsset(assetUSDT);

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.MARKET);
        request.setOrderSide(OrderSide.SELL);
        request.setAmount(new BigDecimal("0.5"));

        BigDecimal marketPrice = new BigDecimal("50000");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(tradingPair);
        when(marketDataService.getCurrentPrice("BTC/USDT")).thenReturn(marketPrice);

        orderService.placeOrder(1L, request);

        verify(walletService).trade(
                eq(1L),
                eq(assetBTC),
                eq(assetUSDT),
                eq(new BigDecimal("0.5")),
                eq(new BigDecimal("25000.00"))
        );
    }

    @Test
    void shouldThrowException_WhenLimitOrder_HasNoPrice() {
        User user = new User(); user.setId(1L);
        TradingPair pair = new TradingPair();

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.LIMIT);
        request.setPrice(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair(anyString())).thenReturn(pair);

        assertThrows(InvalidAmountException.class, () -> orderService.placeOrder(1L, request));
    }

    @Test
    void shouldThrowException_WhenMarket_HasNoPriceData() {

        User user = new User(); user.setId(1L);
        TradingPair pair = new TradingPair();

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.MARKET);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair(anyString())).thenReturn(pair);
        when(marketDataService.getCurrentPrice(anyString())).thenReturn(BigDecimal.ZERO);

        RuntimeException ex = assertThrows(PriceNotFoundException.class, () -> orderService.placeOrder(1L, request));
        assertTrue(ex.getMessage().contains("Price not found: "));
    }
}
