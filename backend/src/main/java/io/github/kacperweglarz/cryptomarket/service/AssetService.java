package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.exception.AssetNotFoundException;
import io.github.kacperweglarz.cryptomarket.repository.AssetRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AssetService {

    private final AssetRepository assetRepository;

    @Autowired
    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    private static final Map<String, String> ASSET_NAMES = new HashMap<>();

    static {
        ASSET_NAMES.put("BTC", "Bitcoin");
        ASSET_NAMES.put("ETH", "Ethereum");
        ASSET_NAMES.put("USDT", "Tether");
        ASSET_NAMES.put("BNB", "Binance Coin");
        ASSET_NAMES.put("DOGE", "Dogecoin");
        ASSET_NAMES.put("ADA", "Cardano");
        ASSET_NAMES.put("XRP", "Ripple");
        ASSET_NAMES.put("SOL", "Solana");
    }

    @PostConstruct
    public void initAssets() {
        ASSET_NAMES.forEach(this::getOrCreateAsset);
        log.info("Assets initialized");
    }

    public Asset getOrCreateAsset(String assetSymbol, String assetName) {

        String upperSymbol = assetSymbol.toUpperCase().trim();

        return assetRepository.findByAssetSymbol(upperSymbol)
                .orElseGet(() -> {
                    log.info("Creating new asset: {}", upperSymbol);

                    Asset newAsset = new Asset();

                    newAsset.setAssetSymbol(upperSymbol);
                    newAsset.setAssetName(assetName != null ? assetName : "Unknown Asset");

                    return assetRepository.save(newAsset);
                });
    }

    public Asset getAsset(String symbol) {
        return assetRepository.findByAssetSymbol(symbol.toUpperCase())
                .orElseThrow(() -> new AssetNotFoundException("Asset not found: " + symbol));
    }
}
