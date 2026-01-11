package io.github.kacperweglarz.cryptomarket.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

        private Long id;
        private List<WalletItemResponse> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletItemResponse {
        private String symbol;
        private String name;
        private BigDecimal amount;
        private BigDecimal available;
        private BigDecimal locked;
    }
}
