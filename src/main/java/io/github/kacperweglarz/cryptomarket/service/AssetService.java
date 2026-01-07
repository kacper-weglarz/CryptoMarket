package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    @Autowired
    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
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
        newAsset.setAssetName(assetName);

        assetRepository.save(newAsset);

        return newAsset;
    }
}
