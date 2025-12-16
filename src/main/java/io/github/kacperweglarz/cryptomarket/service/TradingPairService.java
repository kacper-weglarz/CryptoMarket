package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.repository.TradingPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradingPairService {

    private final TradingPairRepository tradingPairRepository;

    @Autowired
    public TradingPairService(TradingPairRepository tradingPairRepository) {
        this.tradingPairRepository = tradingPairRepository;
    }


    public TradingPair createTradingPair(Asset baseAsset, Asset quoteAsset) {

        String tradingPairSymbol = baseAsset.getAssetSymbol() + "/" + quoteAsset.getAssetSymbol();

        if (baseAsset.getAssetSymbol().equals(quoteAsset.getAssetSymbol())) {
            throw new IllegalArgumentException("TradingPair asset symbols are equal");
        }

        if (tradingPairRepository.existsTradingPair_ByTradingPairSymbol(tradingPairSymbol)) {
            throw new IllegalArgumentException("TradingPair already exists");
        }

        TradingPair newTradingPair = new TradingPair();
        newTradingPair.setBaseAsset(baseAsset);
        newTradingPair.setQuoteAsset(quoteAsset);
        newTradingPair.setTradingPairSymbol(tradingPairSymbol);
        tradingPairRepository.save(newTradingPair);
        return newTradingPair;
    }
}
