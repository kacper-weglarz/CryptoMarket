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
import io.github.kacperweglarz.cryptomarket.exception.PriceNotFoundException;
import io.github.kacperweglarz.cryptomarket.exception.UserNotFoundException;
import io.github.kacperweglarz.cryptomarket.repository.OrderRepository;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
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

    private OrderService self;
    @Autowired
    public void setSelf(@Lazy OrderService self) {
        this.self = self;
    }

    @Transactional
    public SpotOrderResponse placeSpotOrder(Long id, SpotOrderRequest request){

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero " + request.getAmount());
        }

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(""));
        TradingPair tradingPair = tradingPairService.getOrCreateTradingPair(request.getSymbol());

        Order order = new Order();
        order.setUser(user);
        order.setTradingPair(tradingPair);
        order.setAmount(request.getAmount().setScale(CRYPTO_SCALE, RoundingMode.HALF_UP));
        order.setSide(request.getOrderSide());
        order.setType(request.getOrderType());

        if (request.getOrderType() == OrderType.LIMIT) {
            if (request.getPrice() == null) throw new InvalidAmountException("Price cannot be null for LIMIT order");

            Asset assetToLock;
            BigDecimal amountToLock;

            if (request.getOrderSide() == OrderSide.BUY) {
                assetToLock = tradingPair.getQuoteAsset();
                amountToLock = request.getAmount().multiply(request.getPrice()).setScale(USDT_SCALE, RoundingMode.HALF_UP);
            } else {
                assetToLock = tradingPair.getBaseAsset();
                amountToLock = request.getAmount().setScale(CRYPTO_SCALE, RoundingMode.HALF_UP);
            }

            walletService.lockFunds(user.getId(), assetToLock, amountToLock);

            order.setPrice(request.getPrice().setScale(USDT_SCALE, RoundingMode.HALF_UP));
            order.setStatus(OrderStatus.PENDING);
            orderRepository.save(order);

            return new SpotOrderResponse(
                    order.getId(),
                    order.getTradingPair().getTradingPairSymbol(),
                    order.getType(),
                    order.getSide(),
                    order.getAmount(),
                    order.getPrice(),
                    order.getStatus(),
                    order.getCreatedAt());

        } else {
            BigDecimal currentPrice = marketDataService.getCurrentPrice(request.getSymbol());
            if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) throw new PriceNotFoundException(request.getSymbol());

            Asset assetToSpend;
            Asset assetToReceive;
            BigDecimal amountToSpend;
            BigDecimal amountToReceive;

            if (request.getOrderSide() == OrderSide.BUY) {
                assetToSpend = tradingPair.getQuoteAsset();
                assetToReceive = tradingPair.getBaseAsset();
                amountToReceive = request.getAmount().setScale(CRYPTO_SCALE, RoundingMode.HALF_UP);
                amountToSpend = amountToReceive.multiply(currentPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);
            } else {
                assetToSpend = tradingPair.getBaseAsset();
                assetToReceive = tradingPair.getQuoteAsset();
                amountToSpend = request.getAmount().setScale(CRYPTO_SCALE, RoundingMode.HALF_UP);
                amountToReceive = amountToSpend.multiply(currentPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);
            }

            walletService.trade(user.getId(), assetToSpend, assetToReceive, amountToSpend, amountToReceive);

            order.setAmount(request.getAmount().setScale(CRYPTO_SCALE, RoundingMode.HALF_UP));
            order.setPrice(currentPrice.setScale(USDT_SCALE, RoundingMode.HALF_UP));
            order.setStatus(OrderStatus.FILLED);
            orderRepository.save(order);

            return new SpotOrderResponse(
                    order.getId(),
                    order.getTradingPair().getTradingPairSymbol(),
                    order.getType(),
                    order.getSide(),
                    order.getAmount(),
                    order.getPrice(),
                    order.getStatus(),
                    order.getCreatedAt());
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void processSpotLimitOrder() {
        List<Order> orders = orderRepository.findAllByStatusWithRelations(OrderStatus.PENDING);

        orders.forEach(order -> {
            try {
                if (order.getType() != OrderType.LIMIT) return;

                BigDecimal currentPrice = marketDataService.getCurrentPrice(order.getTradingPair().getTradingPairSymbol());

                if (currentPrice == null) return;

                boolean shouldExecute = false;

                if (order.getSide() == OrderSide.BUY) {
                    if (currentPrice.compareTo(order.getPrice()) <= 0) {
                        shouldExecute = true;
                    }
                } else if (order.getSide() == OrderSide.SELL) {
                    if (currentPrice.compareTo(order.getPrice()) >= 0) {
                        shouldExecute = true;
                    }
                }

                if (shouldExecute) {
                    self.executeLimitOrder(order, currentPrice);
                }

            } catch (Exception e) {
                log.error("Błąd procesowania zlecenia ID: " + order.getId(), e);
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void executeLimitOrder(Order order, BigDecimal currentPrice) {
        Long userId = order.getUser().getId();
        BigDecimal amount = order.getAmount().setScale(CRYPTO_SCALE, RoundingMode.HALF_UP);
        BigDecimal limitPrice = order.getPrice().setScale(USDT_SCALE, RoundingMode.HALF_UP);

        if (order.getSide() == OrderSide.BUY) {
            Asset assetLocked = order.getTradingPair().getQuoteAsset();
            BigDecimal amountLocked = amount.multiply(limitPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);
            BigDecimal actualCost = amount.multiply(currentPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);

            walletService.decreaseLockedBalance(userId, assetLocked, amountLocked);
            walletService.depositAsset(userId, order.getTradingPair().getBaseAsset(), amount);

            if (amountLocked.compareTo(actualCost) > 0) {
                BigDecimal refund = amountLocked.subtract(actualCost).setScale(USDT_SCALE, RoundingMode.HALF_UP);
                walletService.depositAsset(userId, assetLocked, refund);
            }
        } else {
            Asset assetLocked = order.getTradingPair().getBaseAsset();
            BigDecimal amountLocked = amount.setScale(CRYPTO_SCALE, RoundingMode.HALF_UP);
            BigDecimal amountReceived = amount.multiply(currentPrice).setScale(USDT_SCALE, RoundingMode.HALF_UP);

            walletService.decreaseLockedBalance(userId, assetLocked, amountLocked);
            walletService.depositAsset(userId, order.getTradingPair().getQuoteAsset(), amountReceived);
        }

        order.setPrice(currentPrice.setScale(USDT_SCALE, RoundingMode.HALF_UP));
        order.setStatus(OrderStatus.FILLED);
        orderRepository.save(order);
        log.info("Zlecenie ID: {} wykonane. Cena: {}", order.getId(), currentPrice);
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
}
