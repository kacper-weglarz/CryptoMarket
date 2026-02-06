package io.github.kacperweglarz.cryptomarket.DTO.request;

import io.github.kacperweglarz.cryptomarket.entity.enums.OrderSide;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotOrderRequest {

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00000001", message = "Amount must be positive")
    private BigDecimal amount;

    @DecimalMin(value = "0.00000001", message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "OrderType is required")
    private OrderType orderType;

    @NotNull(message = "OrderSide is required")
    private OrderSide orderSide;
}
