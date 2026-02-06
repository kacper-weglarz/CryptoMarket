package io.github.kacperweglarz.cryptomarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor @Getter
@Setter @AllArgsConstructor
@Entity
@Table(name = "market_data", indexes ={ @Index(name = "idx_market_data_pair_timestamp", columnList = "trading_pair_id, timestamp")} )
public class MarketData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_pair_id", nullable = false)
    private TradingPair tradingPair;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal open;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal high;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal low;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal close;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal volume;

    @Column(length = 5, nullable = false)
    private String interval;
}
