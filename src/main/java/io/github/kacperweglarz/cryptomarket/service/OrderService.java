package io.github.kacperweglarz.cryptomarket.service;

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
import io.github.kacperweglarz.cryptomarket.exception.UserNotFoundException;
import io.github.kacperweglarz.cryptomarket.repository.OrderRepository;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final TradingPairService tradingPairService;
    private final MarketDataService marketDataService;

    @Autowired
    public OrderService(OrderRepository orderRepository, WalletService walletService, UserRepository userRepository,
                        TradingPairService tradingPairService, MarketDataService marketDataService) {
        this.orderRepository = orderRepository;
        this.walletService = walletService;
        this.userRepository = userRepository;
        this.tradingPairService = tradingPairService;
        this.marketDataService = marketDataService;
    }

    @Transactional
    public void placeOrder(Long id, SpotOrderRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        TradingPair tradingPair = tradingPairService.getOrCreateTradingPair(request.getSymbol());

        Order order = new Order();
        order.setUser(user);
        order.setTradingPair(tradingPair);
        order.setAmount(request.getAmount());
        order.setSide(request.getOrderSide());
        order.setType(request.getOrderType());

        if (request.getOrderType() == OrderType.LIMIT) {
            if (request.getPrice() == null) {
                throw new InvalidAmountException("Price is null in LIMIT order");
            }

            Asset assetToLock;
            BigDecimal amountToLock;

            if (request.getOrderSide() == OrderSide.BUY) {
                assetToLock = tradingPair.getQuoteAsset();
                amountToLock = request.getAmount().multiply(request.getPrice());
            } else {
                assetToLock = tradingPair.getBaseAsset();
                amountToLock = request.getAmount();
            }

            walletService.lockFunds(user.getId(), assetToLock, amountToLock);

            order.setPrice(request.getPrice());
            order.setStatus(OrderStatus.PENDING);
            orderRepository.save(order);

        } else {

            BigDecimal currentPrice = marketDataService.getCurrentPrice(request.getSymbol());

            if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new PriceNotFoundException(request.getSymbol());
            }

            Asset assetToSpend;
            Asset assetToReceive;
            BigDecimal amountToSpend = request.getAmount();
            BigDecimal amountToReceive;

            if (request.getOrderSide() == OrderSide.BUY) {
                assetToSpend = tradingPair.getQuoteAsset();
                assetToReceive = tradingPair.getBaseAsset();
                amountToReceive = amountToSpend.divide(currentPrice, 8, RoundingMode.HALF_DOWN);
            } else {
                assetToSpend = tradingPair.getBaseAsset();
                assetToReceive = tradingPair.getQuoteAsset();
                amountToReceive = amountToSpend.multiply(currentPrice).setScale(2, RoundingMode.HALF_DOWN);
            }

            walletService.trade(user.getId(), assetToSpend, assetToReceive, amountToSpend, amountToReceive);

            order.setPrice(currentPrice);
            order.setStatus(OrderStatus.FILLED);
            orderRepository.save(order);
        }
    }
}
