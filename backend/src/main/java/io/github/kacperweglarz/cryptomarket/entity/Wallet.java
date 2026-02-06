package io.github.kacperweglarz.cryptomarket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor @Getter
@Setter @AllArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WalletItem> walletItems = new ArrayList<>();

    @Column(name = "isInitialized")
    private boolean isInitialized = false;

    @Version
    private Long version;

    @Column(updatable = false) @CreationTimestamp
    private LocalDateTime createdAt;

    @Column @UpdateTimestamp
    private LocalDateTime updatedAt;

}
