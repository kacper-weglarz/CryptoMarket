package io.github.kacperweglarz.cryptomarket.DTO.request;

import io.github.kacperweglarz.cryptomarket.entity.enums.OrderSide;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotOrderRequest {

    @NotNull(message = "Order must have symbol")
    private String symbol;

    @NotNull(message = "Amount cant be null")
    @DecimalMin(value = "0.00000001", message = "Amount must be positive")
    private BigDecimal amount;

    @DecimalMin(value = "0.00000001", message = "Amount must be positive")
    private BigDecimal price;

    @NotNull(message = "OrderType cant be null")
    private OrderType orderType;

    @NotNull(message = "OrderSide cant be null")
    private OrderSide orderSide;
}
