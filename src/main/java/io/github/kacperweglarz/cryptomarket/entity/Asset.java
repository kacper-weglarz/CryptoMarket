package io.github.kacperweglarz.cryptomarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor @Getter
@Setter @AllArgsConstructor
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "asset",  fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WalletItem> walletItems = new ArrayList<>();

    @OneToMany(mappedBy = "baseAsset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TradingPair> baseAssets = new ArrayList<>();

    @OneToMany(mappedBy = "quoteAsset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TradingPair> quoteAssets = new ArrayList<>();

    @Column(name = "symbol", nullable = false, unique = true,  length = 4)
    private String assetSymbol;

    @Column(name = "name",  nullable = false, unique = true, length = 50)
    private String assetName;
}
