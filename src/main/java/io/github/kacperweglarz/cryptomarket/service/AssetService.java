package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        Asset newAsset = new Asset();
        newAsset.setAssetSymbol(assetSymbol);
        newAsset.setAssetName(assetName);
        assetRepository.save(newAsset);
        return newAsset;
    }

}
