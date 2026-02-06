package io.github.kacperweglarz.cryptomarket.repository;

import io.github.kacperweglarz.cryptomarket.entity.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {

    List<MarketData> findByTradingPairTradingPairSymbolAndIntervalAndTimestampBetweenOrderByTimestampAsc(
            String symbol, String interval, Instant start, Instant end
    );
}
