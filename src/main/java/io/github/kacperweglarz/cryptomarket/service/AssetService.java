package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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

    public Asset createAsset(String assetSymbol, String assetName) {

        if (assetRepository.existsByAssetSymbol(assetSymbol)) {
            throw new IllegalArgumentException("Asset Symbol already exists");
        }

        return saveAsset(assetSymbol, assetName);
    }

    public Asset getOrCreateAsset(String assetSymbol, String assetName) {

        Asset existingAsset = assetRepository.getAssetsByAssetSymbol(assetSymbol);

        if (existingAsset != null) {
            return existingAsset;
        }

        return saveAsset(assetSymbol, assetName);
    }

    private Asset saveAsset(String assetSymbol, String assetName) {

        Asset newAsset = new Asset();
        newAsset.setAssetSymbol(assetSymbol);

        String assetNameChanger = ASSET_NAMES.getOrDefault(assetSymbol, assetName);

        newAsset.setAssetName(assetNameChanger);

        assetRepository.save(newAsset);

        return newAsset;
    }
}
