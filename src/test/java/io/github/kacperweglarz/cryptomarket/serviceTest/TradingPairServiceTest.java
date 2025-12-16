package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.repository.TradingPairRepository;
import io.github.kacperweglarz.cryptomarket.service.TradingPairService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradingPairServiceTest {

    @Mock
    private TradingPairRepository tradingPairRepository;

    @InjectMocks
    private TradingPairService tradingPairService;

    @Test
    void shouldCreateTradingPair_WhenSymbolIsUnique_And_AssetsAreNotTheSame() {

        Asset baseAsset = new Asset();
        baseAsset.setAssetSymbol("BTC");

        Asset quoteAsset = new Asset();
        quoteAsset.setAssetSymbol("USDT");

        String tradingPairSymbol = "BTC/USDT";

        when(tradingPairRepository.existsTradingPair_ByTradingPairSymbol(tradingPairSymbol)).thenReturn(Boolean.FALSE);
        when(tradingPairRepository.save(any(TradingPair.class))).thenAnswer(i->i.getArgument(0));

        TradingPair newTradingPair  = tradingPairService.createTradingPair(baseAsset, quoteAsset);

        assertNotNull(newTradingPair);
        assertEquals(tradingPairSymbol, newTradingPair.getTradingPairSymbol());

        verify(tradingPairRepository, times(1)).save(any(TradingPair.class));

    }

    @Test
    void shouldThrowException_WhenSymbolIsUnique_And_AssetsAreTheSame() {

        Asset baseAsset = new Asset();
        baseAsset.setAssetSymbol("BTC");

        Asset quoteAsset = new Asset();
        quoteAsset.setAssetSymbol("BTC");

        assertThrows(IllegalArgumentException.class, () -> tradingPairService.createTradingPair(baseAsset, quoteAsset));

        verify(tradingPairRepository, never()).save(any(TradingPair.class));
    }

    @Test
    void shouldThrowException_WhenSymbolIsNotUnique_And_AssetsAreNotTheSame() {

        Asset baseAsset = new Asset();
        baseAsset.setAssetSymbol("BTC");

        Asset quoteAsset = new Asset();
        quoteAsset.setAssetSymbol("USDT");

        String tradingPairSymbol = "BTC/USDT";

        when(tradingPairRepository.existsTradingPair_ByTradingPairSymbol(tradingPairSymbol)).thenReturn(Boolean.TRUE);

        assertThrows(IllegalArgumentException.class, () -> tradingPairService.createTradingPair(baseAsset, quoteAsset));

        verify(tradingPairRepository, never()).save(any(TradingPair.class));
    }

}
