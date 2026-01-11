package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.MarketData;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.repository.MarketDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketDataService {

    private final MarketDataRepository marketDataRepository;
    private final TradingPairService tradingPairService;

    @Autowired
    public MarketDataService(MarketDataRepository marketDataRepository,  TradingPairService tradingPairService) {
        this.marketDataRepository = marketDataRepository;
        this.tradingPairService = tradingPairService;
    }

    private final Map<String, MarketData> currentCandles = new ConcurrentHashMap<>();
    private final Map<String, TradingPair> knownTradingPairs = new ConcurrentHashMap<>();

    public MarketData updatePrices(String symbol, BigDecimal price, BigDecimal volume) {

        TradingPair thisTradingPair = knownTradingPairs.get(symbol);

        if (thisTradingPair == null) {

            thisTradingPair = tradingPairService.getOrCreateTradingPair(symbol);

            knownTradingPairs.put(symbol, thisTradingPair);
        }

        Instant now = Instant.now();
        Instant candleTimeStamp = now.truncatedTo(ChronoUnit.MINUTES);

        MarketData thisCandle = currentCandles.get(symbol);

        MarketData returnCandle;

        if (thisCandle == null) {

            returnCandle = createCandle(thisTradingPair,price,volume,candleTimeStamp);

            currentCandles.put(symbol, returnCandle);

        } else if (candleTimeStamp.isAfter(thisCandle.getTimestamp())) {

            marketDataRepository.save(thisCandle);

            returnCandle = createCandle(thisTradingPair,price,volume,candleTimeStamp);

            currentCandles.put(symbol, returnCandle);

        } else {

            if (price.compareTo(thisCandle.getHigh()) > 0) {
                thisCandle.setHigh(price);
            }
            if (price.compareTo(thisCandle.getLow()) < 0) {
                thisCandle.setLow(price);
            }
            thisCandle.setClose(price);
            thisCandle.setVolume(thisCandle.getVolume().add(volume));

            returnCandle = thisCandle;
        }

        return returnCandle;
    }



    private MarketData createCandle(TradingPair tradingPair, BigDecimal price, BigDecimal volume, Instant timestamp) {

        MarketData newMarketData = new MarketData();

        newMarketData.setOpen(price);
        newMarketData.setHigh(price);
        newMarketData.setLow(price);
        newMarketData.setClose(price);

        newMarketData.setVolume(volume);

        newMarketData.setTimestamp(timestamp);

        newMarketData.setTradingPair(tradingPair);

        return newMarketData;
    }

    public BigDecimal getCurrentPrice(String symbol) {

        MarketData latestCandle =  currentCandles.get(symbol);

        if (latestCandle != null) {
            return latestCandle.getClose();
        }

        return BigDecimal.ZERO;
    }
}
