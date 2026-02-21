package io.github.kacperweglarz.cryptomarket.integration;

import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import io.github.kacperweglarz.cryptomarket.DTO.request.SpotOrderRequest;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderSide;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderType;
import io.github.kacperweglarz.cryptomarket.repository.OrderRepository;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import io.github.kacperweglarz.cryptomarket.repository.WalletRepository;
import io.github.kacperweglarz.cryptomarket.security.service.AuthService;
import io.github.kacperweglarz.cryptomarket.service.MarketDataService;
import io.github.kacperweglarz.cryptomarket.service.MockDataGenerator;
import io.github.kacperweglarz.cryptomarket.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.math.BigDecimal;
import java.time.Duration;

public class OrderControllerTest extends BaseIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private MarketDataService marketDataService;

    @Autowired
    private MockDataGenerator mockDataGenerator;

    private String token;
    private Long userId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("userName");
        registerRequest.setSurname("userSurname");
        registerRequest.setAlias("userAlias");
        registerRequest.setEmail("user@user");
        registerRequest.setPassword("password");

        var res = authService.registerUser(registerRequest);
        this.token = res.getToken();
        this.userId = res.getUserId();

        walletService.deposit(userId, "USDT", new BigDecimal("100000.00"));
    }

    @Test
    void shouldPlaceMarketOrder() throws Exception {

        marketDataService.updatePrices("BTC/USDT", new BigDecimal("40000.00"), BigDecimal.ZERO, BigDecimal.ZERO);

        SpotOrderRequest spotOrderRequest = new SpotOrderRequest();
        spotOrderRequest.setSymbol("BTC/USDT");
        spotOrderRequest.setAmount(new BigDecimal("0.1"));
        spotOrderRequest.setOrderSide(OrderSide.BUY);
        spotOrderRequest.setOrderType(OrderType.MARKET);

        mockMvc.perform(post("/api/v1/orders/spot")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(spotOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FILLED"))
                .andExpect(jsonPath("$.price").value(40000.00));
    }

    @Test
    void shouldPlaceAndThenExecuteLimitOrder() throws Exception {
        mockDataGenerator.setPaused(true);
        marketDataService.updatePrices("BTC/USDT", new BigDecimal("42000.00"), BigDecimal.ZERO, BigDecimal.ZERO);

        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setAmount(new BigDecimal("1.0"));
        request.setPrice(new BigDecimal("30000.00"));
        request.setOrderSide(OrderSide.BUY);
        request.setOrderType(OrderType.LIMIT);

        mockMvc.perform(post("/api/v1/orders/spot")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        marketDataService.updatePrices("BTC/USDT", new BigDecimal("29000.00"), BigDecimal.ZERO, BigDecimal.ZERO);

        await().atMost(Duration.ofSeconds(5))
                .until(() -> {
                    var orders = orderRepository.findByUserIdOrderByIdDesc(userId);
                    return orders.get(0).getStatus().equals(io.github.kacperweglarz.cryptomarket.entity.enums.OrderStatus.FILLED);
                });
    }

    @Test
    void shouldCancelPendingOrderAndUnlockFunds() throws Exception {

        SpotOrderRequest spotOrderRequest = new SpotOrderRequest();
        spotOrderRequest.setSymbol("BTC/USDT");
        spotOrderRequest.setAmount(BigDecimal.ONE);
        spotOrderRequest.setPrice(new BigDecimal("30000.00"));
        spotOrderRequest.setOrderSide(OrderSide.BUY);
        spotOrderRequest.setOrderType(OrderType.LIMIT);

        String response = mockMvc.perform(post("/api/v1/orders/spot")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(spotOrderRequest)))
                .andReturn().getResponse().getContentAsString();

        long orderId = objectMapper.readTree(response).get("orderId").asLong();

        mockMvc.perform(post("/api/v1/orders/spot/cancel/" + orderId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$[?(@.orderId == " + orderId + ")].status").value("CANCELED"));
    }

    @Test
    void shouldReturnInsufficientFounds() throws Exception {
        SpotOrderRequest request = new SpotOrderRequest();
        request.setSymbol("BTC/USDT");
        request.setAmount(new BigDecimal("100.0"));
        request.setOrderSide(OrderSide.BUY);
        request.setOrderType(OrderType.MARKET);

        mockMvc.perform(post("/api/v1/orders/spot")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Insufficient funds")));
    }
}
