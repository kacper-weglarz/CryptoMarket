package io.github.kacperweglarz.cryptomarket.entity;

import io.github.kacperweglarz.cryptomarket.entity.enums.OrderSide;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderStatus;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderType;
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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_pair_id",  nullable = false)
    private TradingPair tradingPair;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal amount;

    @Column(precision = 19, scale = 8)
    private BigDecimal price;

    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal lockedAmount;

    @Column(nullable = false)
    private int leverage = 1;

    @Version
    private Long version;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderSide side;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(updatable = false) @CreationTimestamp
    private LocalDateTime createdAt;

    @Column @UpdateTimestamp
    private LocalDateTime updatedAt;
}
