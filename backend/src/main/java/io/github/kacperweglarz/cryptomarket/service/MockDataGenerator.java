package io.github.kacperweglarz.cryptomarket.service;

import lombok.Setter;
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

    @Setter
    private boolean paused = false;

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
        if (paused) return;

        mockPrices.forEach((symbol, currentPrice) -> {

            double volatility = 0.005;
            double changeFactor = 1 + (random.nextDouble() - 0.5) * 2 * volatility;

            BigDecimal newRawPrice = currentPrice.multiply(BigDecimal.valueOf(changeFactor));

            int scale = newRawPrice.compareTo(BigDecimal.TEN) < 0 ? 6 : 2;
            BigDecimal newPrice = newRawPrice.setScale(scale, RoundingMode.HALF_UP);

            mockPrices.put(symbol, newPrice);

            BigDecimal fakeVolume = BigDecimal.valueOf(random.nextDouble() * 5.0)
                    .setScale(2, RoundingMode.HALF_UP);


            BigDecimal fakeChange24h = BigDecimal.valueOf((random.nextDouble() - 0.5) * 5.0)
                    .setScale(2, RoundingMode.HALF_UP);

            marketDataService.updatePrices(symbol, newPrice, fakeVolume, fakeChange24h);
        });

        log.debug("Mock market tick processed for {} pairs", mockPrices.size());
    }
}