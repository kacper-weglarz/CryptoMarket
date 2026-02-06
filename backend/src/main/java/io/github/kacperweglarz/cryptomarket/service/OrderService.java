package io.github.kacperweglarz.cryptomarket.service;

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
import io.github.kacperweglarz.cryptomarket.exception.OrderNotFoundException;
import io.github.kacperweglarz.cryptomarket.exception.PriceNotFoundException;
import io.github.kacperweglarz.cryptomarket.exception.UserNotFoundException;
import io.github.kacperweglarz.cryptomarket.repository.OrderRepository;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final TradingPairService tradingPairService;
    private final MarketDataService marketDataService;

    private static final int CRYPTO_SCALE = 8;
    private static final int USDT_SCALE = 2;

    @Autowired
    public OrderService(OrderRepository orderRepository, WalletService walletService, UserRepository userRepository,
                        TradingPairService tradingPairService, MarketDataService marketDataService) {
        this.orderRepository = orderRepository;
        this.walletService = walletService;
        this.userRepository = userRepository;
        this.tradingPairService = tradingPairService;
        this.marketDataService = marketDataService;
    }

    @Autowired
    private @Lazy OrderService self;

    @Transactional
    public SpotOrderResponse placeSpotOrder(Long id, SpotOrderRequest request){

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than or equal to zero: " + request.getAmount());
        }

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(""));
        TradingPair tradingPair = tradingPairService.getOrCreateTradingPair(request.getSymbol());

        Order order = new Order();
        order.setUser(user);
        order.setTradingPair(tradingPair);
        order.setAmount(request.getAmount().setScale(CRYPTO_SCALE, RoundingMode.HALF_UP));
        order.setSide(request.getOrderSide());
        order.setType(request.getOrderType());
        order.setLeverage(1);
        order.setLockedAmount(BigDecimal.ZERO);

        if (request.getOrderType() == OrderType.LIMIT) {
            return processLimitOrder(order, request.getPrice(), tradingPair);
        } else {
            return processMarketOrder(order, tradingPair);
        }
    }

    private SpotOrderResponse processLimitOrder(Order order, BigDecimal price, TradingPair pair) {

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("LIMIT order price is required and must be greater than zero: " + price);
        }

        String assetToLockSymbol;
        BigDecimal amountToLock;

        if (order.getSide() == OrderSide.BUY) {
            assetToLockSymbol = pair.getQuoteAsset().getAssetSymbol();
            amountToLock = order.getAmount().multiply(price).setScale(USDT_SCALE, RoundingMode.HALF_UP);
        } else {
            assetToLockSymbol = pair.getBaseAsset().getAssetSymbol();
            amountToLock = order.getAmount();
        }

        walletService.lockFunds(order.getUser().getId(), assetToLockSymbol, amountToLock);

        order.setPrice(price);
        order.setLockedAmount(amountToLock);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    private SpotOrderResponse processMarketOrder(Order order, TradingPair pair) {

        BigDecimal currentPrice = marketDataService.getCurrentPrice(pair.getTradingPairSymbol());

        if (currentPrice == null) {
            throw new PriceNotFoundException(pair.getTradingPairSymbol());
        }

        String spendSymbol;
        String receiveSymbol;
        BigDecimal spendAmount;
        BigDecimal receiveAmount;

        if (order.getSide() == OrderSide.BUY) {
            spendSymbol = pair.getQuoteAsset().getAssetSymbol();
            receiveSymbol = pair.getBaseAsset().getAssetSymbol();

            receiveAmount = order.getAmount();
            spendAmount = receiveAmount.multiply(currentPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);
        } else {
            spendSymbol = pair.getBaseAsset().getAssetSymbol();
            receiveSymbol = pair.getQuoteAsset().getAssetSymbol();

            spendAmount = order.getAmount();
            receiveAmount = spendAmount.multiply(currentPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);
        }

        walletService.lockFunds(order.getUser().getId(), spendSymbol, spendAmount);
        walletService.withdrawLockedFunds(order.getUser().getId(), spendSymbol, spendAmount);
        walletService.deposit(order.getUser().getId(), receiveSymbol, receiveAmount);

        order.setPrice(currentPrice);
        order.setLockedAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.FILLED);

        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }


    @Scheduled(fixedDelay = 1000)
    public void processSpotLimitOrder() {
        List<Order> orders = orderRepository.findAllByStatusWithRelations(OrderStatus.PENDING);

        for (Order order : orders) {
            try {
                if (order.getLeverage() > 1) {
                    continue;
                }

                BigDecimal currentPrice = marketDataService.getCurrentPrice(order.getTradingPair().getTradingPairSymbol());

                if (currentPrice == null) {
                    continue;
                }

                boolean executable = (order.getSide() == OrderSide.BUY && currentPrice.compareTo(order.getPrice()) <= 0) ||
                        (order.getSide() == OrderSide.SELL && currentPrice.compareTo(order.getPrice()) >= 0);

                if (executable) {
                    self.executeLimitOrderSafe(order.getId(), currentPrice);
                }
            } catch (Exception e) {
                log.error("Error processing order {}", order.getId(), e);
            }
        }
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void executeLimitOrderSafe(Long orderId, BigDecimal currentPrice) {

        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order disappeared"));

        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }

        Long userId = order.getUser().getId();
        TradingPair pair = order.getTradingPair();

        if (order.getSide() == OrderSide.BUY) {

            BigDecimal blockedUSDT = order.getLockedAmount();
            BigDecimal realCost = order.getAmount().multiply(currentPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);
            BigDecimal boughtBTC = order.getAmount();

            walletService.withdrawLockedFunds(userId, pair.getQuoteAsset().getAssetSymbol(), blockedUSDT);
            walletService.deposit(userId, pair.getBaseAsset().getAssetSymbol(), boughtBTC);

            if (blockedUSDT.compareTo(realCost) > 0) {
                BigDecimal refund = blockedUSDT.subtract(realCost);
                walletService.deposit(userId, pair.getQuoteAsset().getAssetSymbol(), refund);
            }

        } else {
            BigDecimal blockedBTC = order.getLockedAmount();
            BigDecimal gainedUSDT = order.getAmount().multiply(currentPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);

            walletService.withdrawLockedFunds(userId, pair.getBaseAsset().getAssetSymbol(), blockedBTC);
            walletService.deposit(userId, pair.getQuoteAsset().getAssetSymbol(), gainedUSDT);
        }

        order.setStatus(OrderStatus.FILLED);

        orderRepository.save(order);

        log.info("Order {} executed at price {}", orderId, currentPrice);
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId ) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));


        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel order. Current status: " + order.getStatus());
        }

        String assetToUnlock;

        if (order.getSide() == OrderSide.BUY) {
            assetToUnlock = order.getTradingPair().getQuoteAsset().getAssetSymbol();
        } else {
            assetToUnlock = order.getTradingPair().getBaseAsset().getAssetSymbol();
        }

        walletService.unlockFunds(userId, assetToUnlock, order.getLockedAmount());

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        log.info("Order {} canceled successfully. Funds returned: {} {}",
                orderId, order.getLockedAmount(), assetToUnlock);
    }

    public List<SpotOrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(order -> new SpotOrderResponse(
                        order.getId(),
                        order.getTradingPair().getTradingPairSymbol(),
                        order.getType(),
                        order.getSide(),
                        order.getAmount(),
                        order.getPrice(),
                        order.getStatus(),
                        order.getCreatedAt()
                ))
                .toList();
    }

    private SpotOrderResponse mapToResponse(Order order) {
        return new SpotOrderResponse(
                order.getId(),
                order.getTradingPair().getTradingPairSymbol(),
                order.getType(),
                order.getSide(),
                order.getAmount(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

}
