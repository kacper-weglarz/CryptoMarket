package io.github.kacperweglarz.cryptomarket.DTO.request;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderSide;
import io.github.kacperweglarz.cryptomarket.entity.enums.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FuturesOrderRequest {

    @NotNull(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00000001", message = "Amount must be positive")
    private BigDecimal amount;

    @DecimalMin(value = "0.00000001", message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    @NotNull(message = "Side is required")
    private OrderSide orderSide;

    @NotNull(message = "Leverage is required")
    @Min(value = 1, message = "Leverage must be at least 1x")
    @Max(value = 100, message = "Leverage cannot exceed 100x")
    private Integer leverage;

    @DecimalMin(value = "0.00000001", message = "Take profit must be positive")
    private BigDecimal takeProfit;

    @DecimalMin(value = "0.00000001", message = "Stop loss must be positive")
    private BigDecimal stopLoss;
}
