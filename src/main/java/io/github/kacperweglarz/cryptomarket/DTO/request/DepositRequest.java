package io.github.kacperweglarz.cryptomarket.DTO.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepositRequest {

    @NotNull(message = "Amount cant be null")
    @DecimalMin(value = "0.00000001", message = "Amount must be positive")
    private BigDecimal amount;
}
