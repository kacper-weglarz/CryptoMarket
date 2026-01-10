package io.github.kacperweglarz.cryptomarket.repository;

import io.github.kacperweglarz.cryptomarket.entity.WalletItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletItemRepository extends JpaRepository<WalletItem,Long> {


    @Query("SELECT wi FROM WalletItem wi WHERE wi.wallet.id = :walletId AND wi.asset.assetSymbol = :symbol")
    Optional<WalletItem> findByWalletIdAndSymbol(@Param("walletId") Long walletId, @Param("symbol") String symbol);

    @Modifying
    @Query("UPDATE WalletItem wi SET wi.amount = wi.amount + :amount, wi.availableBalance = wi.availableBalance + :amount " +
            "WHERE wi.wallet.id = :walletId AND wi.asset.assetSymbol = :symbol")
    int depositFunds(@Param("walletId") Long walletId, @Param("symbol") String symbol, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE WalletItem wi SET wi.amount = wi.amount - :amount, wi.availableBalance = wi.availableBalance - :amount " +
            "WHERE wi.wallet.id = :walletId AND wi.asset.assetSymbol = :symbol AND wi.availableBalance >= :amount")
    int subtractFunds(@Param("walletId") Long walletId, @Param("symbol") String symbol, @Param("amount") BigDecimal amount);


    @Modifying
    @Query("UPDATE WalletItem wi SET wi.availableBalance = wi.availableBalance - :amount, wi.lockedBalance = wi.lockedBalance + :amount " +
            "WHERE wi.wallet.id = :walletId AND wi.asset.assetSymbol = :symbol AND wi.availableBalance >= :amount")
    int lockFunds(@Param("walletId") Long walletId, @Param("symbol") String symbol, @Param("amount") BigDecimal amount);
}
