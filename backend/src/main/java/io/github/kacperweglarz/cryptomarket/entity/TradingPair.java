package io.github.kacperweglarz.cryptomarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor @Getter
@Setter @AllArgsConstructor
@Entity
@Table(name = "trading_pair")
public class TradingPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", unique = true, nullable = false, length = 20)
    private String tradingPairSymbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_asset_id", nullable = false)
    private Asset baseAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_asset_id", nullable = false)
    private Asset quoteAsset;

    @Version
    private Long version;
}
