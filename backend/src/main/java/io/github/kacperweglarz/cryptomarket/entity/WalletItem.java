package io.github.kacperweglarz.cryptomarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor @Getter
@Setter @AllArgsConstructor
@Entity
@Table(name = "wallet_item")
public class WalletItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal lockedBalance = BigDecimal.ZERO;

    @Column(updatable = false) @CreationTimestamp
    private LocalDateTime createdAt;

    @Column @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public BigDecimal getTotalBalance() {
        return availableBalance.add(lockedBalance);
    }
}
