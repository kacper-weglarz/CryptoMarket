package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.repository.AssetRepository;
import io.github.kacperweglarz.cryptomarket.service.AssetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    //CreateAsset
    @Test
    void shouldCreateAssetAsset_WhenSymbolIsUnique() {

        String assetSymbol = "BTC";
        String assetName = "Bitcoin";

        when(assetRepository.existsByAssetSymbol(assetSymbol)).thenReturn(Boolean.FALSE);
        when(assetRepository.save(any(Asset.class))).thenAnswer(i -> i.getArgument(0));

        Asset asset = assetService.createAsset(assetSymbol, assetName);

        assertNotNull(asset);
        assertEquals(assetSymbol, asset.getAssetSymbol());
        assertEquals(assetName, asset.getAssetName());

        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    //CreateAsset
    @Test
    void shouldThrowException_WhenSymbolIsNotUnique() {

        String assetSymbol = "BTC";
        String assetName = "Bitcoin";

        when(assetRepository.existsByAssetSymbol(assetSymbol)).thenReturn(Boolean.TRUE);

        assertThrows(IllegalArgumentException.class, () ->
                assetService.createAsset(assetSymbol, assetName),
                "Asset symbol should be unique");

        verify(assetRepository, never()).save(any(Asset.class));
        }

    //GetORCreateAsset
    @Test
    void shouldReturnExistingAsset_WhenItAlreadyExists() {

        String assetSymbol = "ETH";
        Asset existingAsset = new Asset();
        existingAsset.setAssetSymbol(assetSymbol);

        when(assetRepository.getAssetsByAssetSymbol(assetSymbol)).thenReturn(existingAsset);

        Asset asset = assetService.getOrCreateAsset(assetSymbol, "Ethereum");

        assertEquals(existingAsset, asset);
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void shouldCreateNewAsset_WhenItDoesNotExist() {
        String assetSymbol = "DOGE";
        String assetName = "Dogecoin";

        when(assetRepository.getAssetsByAssetSymbol(assetSymbol)).thenReturn(null);
        when(assetRepository.save(any(Asset.class))).thenAnswer(i -> i.getArgument(0));

        Asset asset = assetService.getOrCreateAsset(assetSymbol, assetName);

        assertNotNull(asset);
        assertEquals(assetSymbol, asset.getAssetSymbol());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }
}
