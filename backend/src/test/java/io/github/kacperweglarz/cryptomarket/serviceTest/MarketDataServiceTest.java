package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.entity.MarketData;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.repository.MarketDataRepository;
import io.github.kacperweglarz.cryptomarket.service.MarketDataService;
import io.github.kacperweglarz.cryptomarket.service.TradingPairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;
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

    private final String symbol = "BTC/USDT";
    private TradingPair tradingPair;

    @BeforeEach
    void setUp() {
        tradingPair = new TradingPair();
        tradingPair.setTradingPairSymbol(symbol);
    }

    @Test
    void shouldCreateInitialCandleAndSendWebSocketUpdate() {
        BigDecimal price = new BigDecimal("50000");
        BigDecimal volume = new BigDecimal("0.5");
        BigDecimal change = new BigDecimal("1.5");

        when(tradingPairService.getOrCreateTradingPair(symbol)).thenReturn(tradingPair);

        MarketData result = marketDataService.updatePrices(symbol, price, volume, change);

        assertNotNull(result);
        assertEquals(price, result.getOpen());
        assertEquals(price, result.getClose());
        assertEquals(volume, result.getVolume());

        ArgumentCaptor<MarketDataService.PriceUpdateDto> dtoCaptor = ArgumentCaptor.forClass(MarketDataService.PriceUpdateDto.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/prices"), dtoCaptor.capture());

        assertEquals(symbol, dtoCaptor.getValue().symbol());
        assertEquals(price, dtoCaptor.getValue().price());
        assertEquals(change, dtoCaptor.getValue().change());
    }

    @Test
    void shouldUpdateHighAndLowInSameMinute() {

        when(tradingPairService.getOrCreateTradingPair(symbol)).thenReturn(tradingPair);

        BigDecimal openPrice = new BigDecimal("100");
        BigDecimal highPrice = new BigDecimal("150");
        BigDecimal lowPrice = new BigDecimal("50");

        marketDataService.updatePrices(symbol, openPrice, BigDecimal.ONE, BigDecimal.ZERO);
        marketDataService.updatePrices(symbol, highPrice, BigDecimal.ONE, BigDecimal.ZERO);

        MarketData result = marketDataService.updatePrices(symbol, lowPrice, BigDecimal.ONE, BigDecimal.ZERO);

        assertEquals(openPrice, result.getOpen());
        assertEquals(highPrice, result.getHigh());
        assertEquals(lowPrice, result.getLow());
        assertEquals(lowPrice, result.getClose());
        assertEquals(new BigDecimal("3"), result.getVolume());
    }

    @Test
    void shouldCloseOldCandleAndCreateNewOneWhenMinuteChanges() {

        when(tradingPairService.getOrCreateTradingPair(symbol)).thenReturn(tradingPair);

        BigDecimal price1 = new BigDecimal("100");
        BigDecimal price2 = new BigDecimal("200");

        MarketData firstCandle = marketDataService.updatePrices(symbol, price1, BigDecimal.ONE, BigDecimal.ZERO);

        Instant firstTimestamp = firstCandle.getTimestamp();

        firstCandle.setTimestamp(firstTimestamp.minus(1, ChronoUnit.MINUTES));

        MarketData secondCandle = marketDataService.updatePrices(symbol, price2, BigDecimal.TEN, BigDecimal.ZERO);

        assertNotSame(firstCandle, secondCandle);
        assertEquals(firstTimestamp, secondCandle.getTimestamp());
        assertEquals(price2, secondCandle.getOpen());

        verify(marketDataRepository).save(firstCandle);
    }

    @Test
    void shouldReturnPriceFromCurrentCandle() {

        when(tradingPairService.getOrCreateTradingPair(symbol)).thenReturn(tradingPair);

        BigDecimal expectedPrice = new BigDecimal("123.45");

        marketDataService.updatePrices(symbol, expectedPrice, BigDecimal.ONE, BigDecimal.ZERO);

        BigDecimal currentPrice = marketDataService.getCurrentPrice(symbol);

        assertEquals(expectedPrice, currentPrice);
    }

    @Test
    void shouldReturnNullPriceForUnknownSymbol() {

        BigDecimal price = marketDataService.getCurrentPrice("NON-EXISTENT");

        assertNull(price);
    }
}
