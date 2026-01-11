package io.github.kacperweglarz.cryptomarket.DTO.response;

import io.github.kacperweglarz.cryptomarket.entity.enums.OrderSide;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderStatus;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String symbol;
    private OrderType type;
    private OrderSide side;
    private BigDecimal amount;
    private BigDecimal price;
    private OrderStatus status;
    private LocalDateTime timestamp;
}
