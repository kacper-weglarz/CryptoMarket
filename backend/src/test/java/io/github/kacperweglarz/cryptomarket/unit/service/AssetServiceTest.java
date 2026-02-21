package io.github.kacperweglarz.cryptomarket.unit.service;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import io.github.kacperweglarz.cryptomarket.exception.AssetNotFoundException;
import io.github.kacperweglarz.cryptomarket.repository.AssetRepository;
import io.github.kacperweglarz.cryptomarket.service.AssetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    AssetRepository assetRepository;

    @InjectMocks
    AssetService assetService;

    //getOrCreateAsset---------------
    @Test
    void shouldReturnExistingAsset() {

        String assetSymbol = "BTC";
        String assetName = "Bitcoin";
        Asset existingAsset = new Asset();
        existingAsset.setAssetSymbol(assetSymbol);
        existingAsset.setAssetName(assetName);

        when(assetRepository.findByAssetSymbol(assetSymbol)).thenReturn(Optional.of(existingAsset));

        Asset asset = assetService.getOrCreateAsset("BTC", "Bitcoin");

        assertEquals(existingAsset, asset);
        verify(assetRepository, never()).save(any(Asset.class));
        verify(assetRepository, times(1)).findByAssetSymbol(assetSymbol);
    }

    @Test
    void shouldReturnNewAsset() {

        String assetSymbol = "BTC";
        String assetName = "Bitcoin";

        when(assetRepository.findByAssetSymbol(assetSymbol)).thenReturn(Optional.empty());
        when(assetRepository.save(any(Asset.class))).thenAnswer(i -> i.getArgument(0));

        Asset asset = assetService.getOrCreateAsset("BTC", "Bitcoin");

        assertNotNull(asset);
        assertEquals("BTC", asset.getAssetSymbol());
        assertEquals("Bitcoin", asset.getAssetName());


        verify(assetRepository).save(argThat(a ->
                a.getAssetSymbol().equals(assetSymbol) &&
                a.getAssetName().equals(assetName)
        ));
    }

   @Test
    void shouldSetUnknownAssetName() {

       String assetSymbol = "BTC";

       when(assetRepository.findByAssetSymbol(assetSymbol)).thenReturn(Optional.empty());
       when(assetRepository.save(any(Asset.class))).thenAnswer(i -> i.getArgument(0));

       Asset asset = assetService.getOrCreateAsset(assetSymbol, null);

       assertNotNull(asset);
       assertEquals("BTC", asset.getAssetSymbol());
       assertEquals("Unknown Asset", asset.getAssetName());

       verify(assetRepository).save(argThat(a ->
               a.getAssetSymbol().equals(assetSymbol) &&
               a.getAssetName().equals("Unknown Asset")));
   }
    //End getOrCreateAsset---------------



    //getAsset --------------------------
    @Test
    void shouldReturnAsset() {

        String assetSymbol = "BTC";
        String assetName = "Bitcoin";

        Asset asset = new Asset();
        asset.setAssetSymbol(assetSymbol);
        asset.setAssetName(assetName);

        when(assetRepository.findByAssetSymbol(assetSymbol)).thenReturn(Optional.of(asset));

        Asset returnedAsset = assetService.getAsset(assetSymbol);

        assertEquals(asset, returnedAsset);
        verify(assetRepository, times(1)).findByAssetSymbol(assetSymbol);
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void shouldThrowException_WhenAssetDoesNotExist() {

        String assetSymbol = "BTC";

        when(assetRepository.findByAssetSymbol(assetSymbol)).thenReturn(Optional.empty());

        assertThrows(AssetNotFoundException.class, () -> assetService.getAsset(assetSymbol));
        verify(assetRepository, times(1)).findByAssetSymbol(assetSymbol);
    }
    //End getAsset -----------------------
}
