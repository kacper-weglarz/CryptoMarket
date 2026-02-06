package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.MarketData;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.repository.MarketDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MarketDataService {

    private final MarketDataRepository marketDataRepository;
    private final TradingPairService tradingPairService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MarketDataService(MarketDataRepository marketDataRepository,  TradingPairService tradingPairService, SimpMessagingTemplate messagingTemplate) {
        this.marketDataRepository = marketDataRepository;
        this.tradingPairService = tradingPairService;
        this.messagingTemplate = messagingTemplate;
    }

    private final Map<String, MarketData> currentCandles = new ConcurrentHashMap<>();
    private final Map<String, TradingPair> knownTradingPairs = new ConcurrentHashMap<>();

    public MarketData updatePrices(String symbol, BigDecimal price, BigDecimal volume, BigDecimal change) {

        TradingPair pair = knownTradingPairs.computeIfAbsent(symbol, tradingPairService::getOrCreateTradingPair);

        Instant now = Instant.now();
        Instant candleTimeStamp = now.truncatedTo(ChronoUnit.MINUTES);

        MarketData currentCandle = currentCandles.get(symbol);

        if (currentCandle == null) {

            currentCandle = createCandle(pair, price, volume, candleTimeStamp);
            currentCandles.put(symbol, currentCandle);

            log.info("Creating new candle for {}: Open Price {}", symbol, price);

        } else if (candleTimeStamp.isAfter(currentCandle.getTimestamp())) {

            log.info("Closing candle for {}: Close Price {}", symbol, currentCandle.getClose());

            //marketDataRepository.save(currentCandle);

            currentCandle = createCandle(pair, price, volume, candleTimeStamp);
            currentCandles.put(symbol, currentCandle);

        } else {

            updateExistingCandle(currentCandle, price, volume);

            log.info("Updating candle for {}: Close Price {}", symbol, currentCandle.getClose());
        }

        PriceUpdateDto update = new PriceUpdateDto(symbol, price, change, volume);

        messagingTemplate.convertAndSend("/topic/prices", update);

        return currentCandle;
    }

    private void updateExistingCandle(MarketData candle, BigDecimal price, BigDecimal volume) {
        if (price.compareTo(candle.getHigh()) > 0) {
            candle.setHigh(price);
        }

        if (price.compareTo(candle.getLow()) < 0) {
            candle.setLow(price);
        }

        candle.setClose(price);
        candle.setVolume(candle.getVolume().add(volume));
    }

    private MarketData createCandle(TradingPair pair, BigDecimal price, BigDecimal volume, Instant timestamp) {
        MarketData marketData = new MarketData();

        marketData.setTradingPair(pair);
        marketData.setTimestamp(timestamp);
        marketData.setOpen(price);
        marketData.setHigh(price);
        marketData.setLow(price);
        marketData.setClose(price);
        marketData.setVolume(volume);
        marketData.setInterval("1m");

        return marketData;
    }

    public BigDecimal getCurrentPrice(String symbol) {
        MarketData latest = currentCandles.get(symbol);
        return (latest != null) ? latest.getClose() : null;
    }

    public record PriceUpdateDto(String symbol, BigDecimal price, BigDecimal change, BigDecimal volume) {}
}

