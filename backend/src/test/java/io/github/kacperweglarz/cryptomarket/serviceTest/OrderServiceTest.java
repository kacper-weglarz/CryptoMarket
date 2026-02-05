package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.DTO.request.SpotOrderRequest;
import io.github.kacperweglarz.cryptomarket.DTO.response.OrderResponse;
import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.Order;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderSide;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderStatus;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderType;
import io.github.kacperweglarz.cryptomarket.exception.InsufficientFundsException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
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

    private BigDecimal bd(String val) {
        return new BigDecimal(val);
    }

    @Test
    void shouldPlace_LimitOrder_Successfully() {
        User user = new User(); user.setId(1L);
        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");
        Asset assetBTC = new Asset(); assetBTC.setAssetSymbol("BTC");

        TradingPair tradingPair = new TradingPair();
        tradingPair.setBaseAsset(assetBTC);
        tradingPair.setQuoteAsset(assetUSDT);
        tradingPair.setTradingPairSymbol("BTC/USDT");

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setAmount(new BigDecimal("0.5"));
        request.setOrderType(OrderType.LIMIT);
        request.setOrderSide(OrderSide.BUY);
        request.setPrice(new BigDecimal("40000"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(tradingPair);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(100L);
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        OrderResponse response = orderService.placeSpotOrder(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.getOrderId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(0, new BigDecimal("40000").compareTo(response.getPrice()));

        verify(walletService).lockFunds(
                eq(1L),
                eq(assetUSDT),
                argThat(val -> val.compareTo(new BigDecimal("20000")) == 0)
        );
        verify(walletService, never()).trade(any(), any(), any(), any(), any());
    }

    @Test
    void shouldPlace_MarketBuyOrder_Successfully() {
        User user = new User(); user.setId(1L);
        Asset assetBTC = new Asset(); assetBTC.setAssetSymbol("BTC");
        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");

        TradingPair tradingPair = new TradingPair();
        tradingPair.setBaseAsset(assetBTC);
        tradingPair.setQuoteAsset(assetUSDT);
        tradingPair.setTradingPairSymbol("BTC/USDT");

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.MARKET);
        request.setOrderSide(OrderSide.BUY);
        request.setAmount(new BigDecimal("1000"));

        BigDecimal marketPrice = new BigDecimal("50000");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(tradingPair);
        when(marketDataService.getCurrentPrice("BTC/USDT")).thenReturn(marketPrice);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(200L);
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        OrderResponse response = orderService.placeSpotOrder(1L, request);

        assertNotNull(response);
        assertEquals(200L, response.getOrderId());
        assertEquals(OrderStatus.FILLED, response.getStatus());
        assertEquals(0, marketPrice.compareTo(response.getPrice()));

        verify(walletService).trade(
                eq(1L),
                eq(assetUSDT),
                eq(assetBTC),
                argThat(val -> val.compareTo(new BigDecimal("50000000")) == 0),
                argThat(val -> val.compareTo(new BigDecimal("1000")) == 0)
        );
    }

    @Test
    void shouldPlace_MarketSellOrder_Successfully() {
        User user = new User(); user.setId(1L);
        Asset assetBTC = new Asset(); assetBTC.setAssetSymbol("BTC");
        Asset assetUSDT = new Asset(); assetUSDT.setAssetSymbol("USDT");

        TradingPair tradingPair = new TradingPair();
        tradingPair.setBaseAsset(assetBTC);
        tradingPair.setQuoteAsset(assetUSDT);
        tradingPair.setTradingPairSymbol("BTC/USDT");

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.MARKET);
        request.setOrderSide(OrderSide.SELL);
        request.setAmount(new BigDecimal("0.5"));

        BigDecimal marketPrice = new BigDecimal("50000");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(tradingPair);
        when(marketDataService.getCurrentPrice("BTC/USDT")).thenReturn(marketPrice);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(300L);
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        OrderResponse response = orderService.placeSpotOrder(1L, request);

        assertNotNull(response);
        assertEquals(300L, response.getOrderId());
        assertEquals(OrderStatus.FILLED, response.getStatus());

        verify(walletService).trade(
                eq(1L),
                eq(assetBTC),
                eq(assetUSDT),
                argThat(val -> val.compareTo(new BigDecimal("0.5")) == 0),
                argThat(val -> val.compareTo(new BigDecimal("25000")) == 0)
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
        request.setAmount(new BigDecimal("1.0"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair(anyString())).thenReturn(pair);

        assertThrows(InvalidAmountException.class, () -> orderService.placeSpotOrder(1L, request));
    }

    @Test
    void shouldThrowException_WhenMarket_HasNoPriceData() {
        User user = new User(); user.setId(1L);
        TradingPair pair = new TradingPair();

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.MARKET);
        request.setAmount(new BigDecimal("1.0"));
        request.setPrice(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair(anyString())).thenReturn(pair);
        when(marketDataService.getCurrentPrice(anyString())).thenReturn(null);

        assertThrows(PriceNotFoundException.class, () -> orderService.placeSpotOrder(1L, request));
    }

    @Test
    void shouldGetUserOrders_Successfully() {
        Long id = 1L;
        User user = new User(); user.setId(id);

        TradingPair pair = new TradingPair();
        pair.setTradingPairSymbol("BTC/USDT");

        Order order1 = new Order();
        order1.setId(101L);
        order1.setUser(user);
        order1.setTradingPair(pair);
        order1.setType(OrderType.LIMIT);
        order1.setSide(OrderSide.BUY);
        order1.setAmount(new BigDecimal("0.5"));
        order1.setPrice(new BigDecimal("40000"));
        order1.setStatus(OrderStatus.PENDING);
        order1.setCreatedAt(LocalDateTime.now());

        Order order2 = new Order();
        order2.setId(102L);
        order2.setUser(user);
        order2.setTradingPair(pair);
        order2.setType(OrderType.MARKET);
        order2.setSide(OrderSide.SELL);
        order2.setAmount(new BigDecimal("1.0"));
        order2.setPrice(new BigDecimal("42000"));
        order2.setStatus(OrderStatus.FILLED);
        order2.setCreatedAt(LocalDateTime.now().minusHours(1));

        when(orderRepository.findByUserIdOrderByIdDesc(id))
                .thenReturn(List.of(order1, order2));

        List<OrderResponse> responses = orderService.getUserOrders(id);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        OrderResponse res1 = responses.get(0);
        assertEquals(101L, res1.getOrderId());
        assertEquals("BTC/USDT", res1.getSymbol());
        assertEquals(0, new BigDecimal("40000").compareTo(res1.getPrice()));

        OrderResponse res2 = responses.get(1);

        assertEquals(102L, res2.getOrderId());
        assertEquals(OrderStatus.FILLED, res2.getStatus());

        verify(orderRepository, times(1)).findByUserIdOrderByIdDesc(id);
    }

    @Test
    void shouldThrowException_WhenAmountIsZeroOrNegative() {
        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setAmount(new BigDecimal("0.00000000"));
        request.setOrderType(OrderType.MARKET);

        assertThrows(InvalidAmountException.class, () -> orderService.placeSpotOrder(1L, request));
    }

    @Test
    void shouldHandlePriceNotFound_DuringScheduledTask() {
        Order order = new Order();
        order.setId(1L);
        TradingPair pair = new TradingPair();
        pair.setTradingPairSymbol("BTC/USDT");
        order.setTradingPair(pair);
        order.setType(OrderType.LIMIT);

        when(orderRepository.findAllByStatusWithRelations(OrderStatus.PENDING))
                .thenReturn(List.of(order));
        when(marketDataService.getCurrentPrice("BTC/USDT")).thenReturn(null);

        orderService.processSpotLimitOrder();

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_WhenUserHasInsufficientFunds_ToLock() {
        User user = new User(); user.setId(1L);
        Asset usdt = new Asset(); usdt.setAssetSymbol("USDT");
        TradingPair pair = new TradingPair();
        pair.setQuoteAsset(usdt);
        pair.setTradingPairSymbol("BTC/USDT");

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.LIMIT);
        request.setOrderSide(OrderSide.BUY);
        request.setAmount(new BigDecimal("1"));
        request.setPrice(new BigDecimal("50000"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair(anyString())).thenReturn(pair);

        doThrow(new InsufficientFundsException("USDT"))
                .when(walletService).lockFunds(anyLong(), any(), any());

        assertThrows(InsufficientFundsException.class, () -> orderService.placeSpotOrder(1L, request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldHandleHighPrecisionTrade() {
        User user = new User(); user.setId(1L);
        Asset btc = new Asset(); btc.setAssetSymbol("BTC");
        Asset usdt = new Asset(); usdt.setAssetSymbol("USDT");
        TradingPair pair = new TradingPair();
        pair.setBaseAsset(btc); pair.setQuoteAsset(usdt);
        pair.setTradingPairSymbol("BTC/USDT");

        BigDecimal highPrecisionAmount = new BigDecimal("0.00001234");
        BigDecimal currentPrice = new BigDecimal("48550.55123456");

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setOrderType(OrderType.MARKET);
        request.setOrderSide(OrderSide.SELL);
        request.setAmount(highPrecisionAmount);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tradingPairService.getOrCreateTradingPair("BTC/USDT")).thenReturn(pair);
        when(marketDataService.getCurrentPrice("BTC/USDT")).thenReturn(currentPrice);
        when(orderRepository.save(any())).thenReturn(new Order());

        orderService.placeSpotOrder(1L, request);

        BigDecimal expectedReceive = highPrecisionAmount.multiply(currentPrice).setScale(2, RoundingMode.HALF_UP);

        verify(walletService).trade(
                eq(1L),
                eq(btc),
                eq(usdt),
                argThat(v -> v.compareTo(highPrecisionAmount) == 0),
                argThat(v -> v.compareTo(expectedReceive) == 0)
        );
    }
}