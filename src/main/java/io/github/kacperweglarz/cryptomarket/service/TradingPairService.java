package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import io.github.kacperweglarz.cryptomarket.repository.TradingPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradingPairService {

    private final TradingPairRepository tradingPairRepository;
    private final AssetService assetService;

    @Autowired
    public TradingPairService(TradingPairRepository tradingPairRepository, AssetService assetService) {
        this.tradingPairRepository = tradingPairRepository;
        this.assetService = assetService;
    }


    public TradingPair createTradingPair(Asset baseAsset, Asset quoteAsset) {

        String tradingPairSymbol = baseAsset.getAssetSymbol() + "/" + quoteAsset.getAssetSymbol();

        if (baseAsset.getAssetSymbol().equals(quoteAsset.getAssetSymbol())) {
            throw new IllegalArgumentException("TradingPair asset symbols are equal");
        }

        if (tradingPairRepository.existsTradingPair_ByTradingPairSymbol(tradingPairSymbol)) {
            throw new IllegalArgumentException("TradingPair already exists");
        }

        return saveTradingPair(baseAsset, quoteAsset, tradingPairSymbol);
    }

    @Transactional
    public TradingPair getOrCreateTradingPair(Asset baseAsset, Asset quoteAsset) {

        String tradingPairSymbol = baseAsset.getAssetSymbol() + "/" + quoteAsset.getAssetSymbol();

        TradingPair existingTradingPair = tradingPairRepository.findByTradingPairSymbol(tradingPairSymbol);

        if (existingTradingPair != null) {
            return existingTradingPair;
        }

        Asset newBaseAsset = assetService.getOrCreateAsset(baseAsset.getAssetSymbol(), baseAsset.getAssetName());
        Asset newQuoteAsset = assetService.getOrCreateAsset(quoteAsset.getAssetSymbol(), quoteAsset.getAssetName());

        return saveTradingPair(newBaseAsset, newQuoteAsset, tradingPairSymbol);
    }



    public TradingPair saveTradingPair(Asset baseAsset, Asset quoteAsset, String symbol) {

        TradingPair newTradingPair = new TradingPair();
        newTradingPair.setBaseAsset(baseAsset);
        newTradingPair.setQuoteAsset(quoteAsset);
        newTradingPair.setTradingPairSymbol(symbol);

        tradingPairRepository.save(newTradingPair);

        return newTradingPair;
    }

    public TradingPair getTradingPairByTradingPairSymbol(String tradingPairSymbol) {
        return tradingPairRepository.findByTradingPairSymbol(tradingPairSymbol);
    }
}
