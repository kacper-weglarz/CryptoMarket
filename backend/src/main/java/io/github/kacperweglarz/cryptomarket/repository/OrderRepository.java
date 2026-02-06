package io.github.kacperweglarz.cryptomarket.repository;

import io.github.kacperweglarz.cryptomarket.entity.Order;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdOrderByIdDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.user " +
            "JOIN FETCH o.tradingPair tp " +
            "JOIN FETCH tp.baseAsset " +
            "JOIN FETCH tp.quoteAsset " +
            "WHERE o.status = :status")
    List<Order> findAllByStatusWithRelations(@Param("status") OrderStatus status);
}

