package io.github.kacperweglarz.cryptomarket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@Profile("test")
@EnableScheduling
@Slf4j
public class MockDataGenerator {

    private final MarketDataService marketDataService;
    private final Random random = new Random();


    private final Map<String, BigDecimal> mockPrices = new HashMap<>();

    public MockDataGenerator(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;

        mockPrices.put("BTC/USDT", new BigDecimal("42000.00"));
        mockPrices.put("ETH/USDT", new BigDecimal("2300.00"));
        mockPrices.put("SOL/USDT", new BigDecimal("95.00"));
        mockPrices.put("BNB/USDT", new BigDecimal("310.00"));
        mockPrices.put("ADA/USDT", new BigDecimal("0.50"));
        mockPrices.put("XRP/USDT", new BigDecimal("0.70"));
        mockPrices.put("DOGE/USDT", new BigDecimal("5.00"));
    }

    @Scheduled(fixedRate = 1000)
    public void generateFakeData() {

        mockPrices.forEach((symbol, currentPrice) -> {
            double changeFactor = 1 + (random.nextDouble() - 0.5) * 0.01;

            BigDecimal newRawPrice = currentPrice.multiply(BigDecimal.valueOf(changeFactor));

            int scale = newRawPrice.compareTo(BigDecimal.TEN) < 0 ? 4 : 2;

            BigDecimal newPrice = newRawPrice.setScale(scale, RoundingMode.HALF_UP);

            mockPrices.put(symbol, newPrice);

            BigDecimal fakeChange = BigDecimal.valueOf((random.nextDouble() - 0.5) * 10.0)
                    .setScale(2, RoundingMode.HALF_UP);

            marketDataService.updatePrices(symbol, newPrice, BigDecimal.TEN, fakeChange);

        });

        log.info("Mock data sent for {} pairs", mockPrices.size());
    }
}