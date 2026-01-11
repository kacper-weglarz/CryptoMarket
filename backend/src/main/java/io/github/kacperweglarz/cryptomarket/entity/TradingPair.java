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
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "tradingPair")
    private List<Order> orders;

    @OneToMany(mappedBy = "tradingPair", fetch = FetchType.LAZY)
    private List<MarketData> marketData;

    @Column(name = "symbol")
    private String tradingPairSymbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_asset_id")
    private Asset baseAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_asset_id")
    private Asset quoteAsset;
}
