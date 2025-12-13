package io.github.kacperweglarz.cryptomarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor @Data
@Entity @AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 55)
    private String name;

    @Column(length = 55)
    private String username;

    @Column(length = 55)
    private String alias;

    @Column(length = 105, unique = true)
    private String email;

    @Column(length = 55)
    private String passwordHash;

    private boolean twoFactorAuthEnabled;

    private String totpSecret;

//    @Enumerated(EnumType.STRING)
//    private KycStatus kycStatus;

    @Column @CreationTimestamp
    private LocalDateTime createdAt;

    @Column @UpdateTimestamp
    private LocalDateTime updatedAt;
}
