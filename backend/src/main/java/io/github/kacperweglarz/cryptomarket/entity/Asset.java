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
    private Long id;

    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    private String assetSymbol;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String assetName;

    @Version
    private Long version;
}
