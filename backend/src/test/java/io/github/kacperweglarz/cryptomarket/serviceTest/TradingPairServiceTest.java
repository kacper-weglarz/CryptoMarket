package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.repository.TradingPairRepository;
import io.github.kacperweglarz.cryptomarket.service.AssetService;
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
class TradingPairServiceTest {

    @Mock
    TradingPairRepository tradingPairRepository;

    @Mock
    AssetService assetService;

    @InjectMocks
    TradingPairService tradingPairService;


    //CreateTradingPair
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


    //GetORCreateTradingPair
    @Test
    void shouldReturnTradingPair_WhenTradingPairAlreadyExists() {

        Asset base = new Asset();
        base.setAssetSymbol("ETH");
        Asset quote = new Asset();
        quote.setAssetSymbol("USD");

        String symbol = "ETH/USD";

        TradingPair existingPair = new TradingPair();
        existingPair.setTradingPairSymbol(symbol);

        when(tradingPairRepository.findByTradingPairSymbol(symbol)).thenReturn(existingPair);

        TradingPair tradingPair = tradingPairService.getOrCreateTradingPair(symbol);

        assertEquals(existingPair, tradingPair);
        verify(tradingPairRepository, never()).save(any());
        verify(assetService, never()).getOrCreateAsset(any(), any());
    }

    @Test
    void shouldCreateNewTradingPair_WhenTradingPairNotExist() {

        Asset baseAsset = new Asset();
        baseAsset.setAssetSymbol("DOGE");
        baseAsset.setAssetName("Dogecoin");
        Asset quoteAsset = new Asset();
        quoteAsset.setAssetSymbol("USDT");
        quoteAsset.setAssetName("Tether");

        String symbol = "DOGE/USDT";

        when(tradingPairRepository.findByTradingPairSymbol(symbol)).thenReturn(null);
        when(assetService.getOrCreateAsset("DOGE", "DOGE")).thenReturn(baseAsset);
        when(assetService.getOrCreateAsset("USDT", "USDT")).thenReturn(quoteAsset);
        when(tradingPairRepository.save(any(TradingPair.class))).thenAnswer(i -> i.getArgument(0));

        TradingPair result = tradingPairService.getOrCreateTradingPair(symbol);

        assertNotNull(result);
        assertEquals(symbol, result.getTradingPairSymbol());
        verify(assetService, times(2)).getOrCreateAsset(anyString(), anyString());
        verify(tradingPairRepository, times(1)).save(any(TradingPair.class));
    }

}
