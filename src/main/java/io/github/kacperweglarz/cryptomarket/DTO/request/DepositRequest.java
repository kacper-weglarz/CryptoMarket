package io.github.kacperweglarz.cryptomarket.DTO.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepositRequest {

    private BigDecimal amount;
}
