package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.entity.MarketData;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.repository.MarketDataRepository;
import io.github.kacperweglarz.cryptomarket.service.MarketDataService;
import io.github.kacperweglarz.cryptomarket.service.TradingPairService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketDataServiceTest {

    @Mock
    MarketDataRepository marketDataRepository;

    @Mock
    TradingPairService tradingPairService;

    @InjectMocks
    MarketDataService marketDataService;

    @Mock
    SimpMessagingTemplate messagingTemplate;


    @Test
    void shouldCreateNewCandle_And_KnownTradingPair() {

        String symbol = "BTC/USDT";
        BigDecimal price = new BigDecimal(100);
        BigDecimal volume = new BigDecimal(1);
        BigDecimal change = new BigDecimal(2);
        TradingPair tradingPair = new TradingPair();
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);

        when(tradingPairService.getOrCreateTradingPair(symbol)).thenReturn(tradingPair);

        MarketData marketData = marketDataService.updatePrices(symbol, price, volume, change);

        assertNotNull(marketData);
        assertEquals(marketData.getOpen(), price);
        assertEquals(marketData.getHigh(), price);
        assertEquals(marketData.getLow(), price);
        assertEquals(marketData.getClose(), price);
        assertEquals(marketData.getVolume(), volume);
        assertEquals(marketData.getTradingPair(), tradingPair);
        assertEquals(marketData.getTimestamp(), now);

        verify(tradingPairService, times(1)).getOrCreateTradingPair(symbol);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/prices"), any(MarketDataService.PriceUpdateDto.class));
    }

    @Test
    void shouldSaveCandle_And_CreateNewOne() {

        String symbol = "BTCUSD";
        BigDecimal price1 = new BigDecimal(100);
        BigDecimal volume1 = new BigDecimal(1);
        BigDecimal price2 = new BigDecimal(110);
        BigDecimal volume2 = new BigDecimal(2);
        BigDecimal change = new BigDecimal(2);
        BigDecimal change2 = new BigDecimal(2);
        TradingPair pair = new TradingPair();

        Instant oldTimeStamp = Instant.now().minus(1, ChronoUnit.MINUTES);
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);

        when(tradingPairService.getOrCreateTradingPair(symbol)).thenReturn(pair);

        MarketData oldCandle = marketDataService.updatePrices(symbol, price1, volume1, change);

        oldCandle.setTimestamp(oldTimeStamp);

        MarketData marketData = marketDataService.updatePrices(symbol, price2, volume2, change2);

        verify(marketDataRepository).save(oldCandle);

        assertNotNull(marketData);
        assertEquals(price2, marketData.getOpen());
        assertEquals(price2, marketData.getHigh());
        assertEquals(price2, marketData.getLow());
        assertEquals(price2, marketData.getClose());
        assertEquals(volume2, marketData.getVolume());
        assertEquals(pair, marketData.getTradingPair());
        assertEquals(marketData.getTimestamp(), now);
    }

    @Test
    void shouldUpdateCandle_InSameMinute() {

        String symbol = "BTC/USDT";
        BigDecimal firstPrice = new BigDecimal(100);
        BigDecimal newPrice = new BigDecimal(150);
        BigDecimal firstVolume = new BigDecimal(1);
        BigDecimal newVolume = new BigDecimal(12);
        BigDecimal change = new BigDecimal(2);
        TradingPair tradingPair = new TradingPair();
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);

        when(tradingPairService.getOrCreateTradingPair(symbol)).thenReturn(tradingPair);

        marketDataService.updatePrices(symbol, firstPrice, firstVolume, change);

        MarketData marketData = marketDataService.updatePrices(symbol, newPrice, newVolume, change);

        assertNotNull(marketData);
        assertEquals(marketData.getOpen(), firstPrice);
        assertEquals(marketData.getHigh(), newPrice);
        assertEquals(marketData.getLow(), firstPrice);
        assertEquals(marketData.getClose(), newPrice);
        assertEquals(marketData.getVolume(), firstVolume.add(newVolume));
        assertEquals(marketData.getTradingPair(), tradingPair);
        assertEquals(marketData.getTimestamp(), now);

        verify(tradingPairService, times(1)).getOrCreateTradingPair(symbol);
    }
}
