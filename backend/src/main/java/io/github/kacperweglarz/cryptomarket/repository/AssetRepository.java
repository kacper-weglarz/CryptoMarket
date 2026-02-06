package io.github.kacperweglarz.cryptomarket.repository;

import io.github.kacperweglarz.cryptomarket.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    boolean existsByAssetSymbol(String assetSymbol);

    Optional<Asset> findByAssetSymbol(String assetSymbol);
}
