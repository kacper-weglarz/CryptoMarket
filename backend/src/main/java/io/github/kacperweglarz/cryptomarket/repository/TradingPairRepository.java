package io.github.kacperweglarz.cryptomarket.repository;

import io.github.kacperweglarz.cryptomarket.entity.TradingPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradingPairRepository extends JpaRepository<TradingPair, Long> {

    boolean existsTradingPair_ByTradingPairSymbol(String tradingPairSymbol);

    TradingPair  findByTradingPairSymbol(String tradingPairSymbol);
}
